<template>
  <div class="home">
    <!-- 左侧菜单 - 区分文件类型 -->
    <SideMenu class="home-left" :storageValue="storageValue" :storageMaxValue="storageMaxValue"></SideMenu>
    <!-- 右侧内容区 -->
    <div class="home-right">
      <div class="operation-wrapper">
        <OperationMenu 
          :fileType="fileType" 
          :filePath="filePath" 
          @getTableData="getFileData" 
          @handleUploadFile="handleUploadFile"
          :operationFileList="operationFileList"
          @handleSelectFile="setOperationFile"
          @handleMoveFile="setMoveFileDialog"
          ></OperationMenu>
        <ShowModel :fileType="fileType"></ShowModel>
        <SelectColumn></SelectColumn>
      </div>
      <!-- 面包屑导航栏 - 显示文件路径 -->
      <BreadCrumb :fileType="fileType"></BreadCrumb>
      <!-- 表格组件 - 文件展示区 -->
      <FileTable
        v-if="(fileType === 1 && showModel === 0) || fileType !== 1" 
        :tableData="tableData"
        :loading="loading"
        :fileType="fileType"
        @getTableData="getFileData"
        @handleSelectFile="setOperationFile"
        @handleMoveFile="setMoveFileDialog"
        ></FileTable>
        <FileGrid v-if="fileType === 1 && showModel === 1" :tableData="tableData" :loading="loading"></FileGrid>
        <FileTimeLine v-if="fileType === 1 && showModel === 2" :tableData="tableData" :loading="loading"></FileTimeLine>
      <!-- 文件分页 -->
      <FilePagination
        :pageData="pageData"
        @changePageData="changePageData"
      ></FilePagination>
      <FileUploader ref="globalUploader" @getTableData="getFileData"></FileUploader>
    </div>
    <!-- 移动文件模态框 -->
    <MoveFileDialog
      :dialogMoveFile="dialogMoveFile"
      @setSelectFilePath="setSelectFilePath"
      @confirmMoveFile="confirmMoveFile"
      @handleMoveFile="setMoveFileDialog"
    ></MoveFileDialog>
    <ImgReview></ImgReview>
  </div>
</template>

<script>
import { getFileListByPath, getFileListByType } from '@/request/file.js' //  引入获取文件列表接口
import { getFileStorage } from '@/request/file.js' //  引入查询存储空间接口
import { getUserMaxStorage } from '@/request/file.js' // 引入查询用户存储空间接口


import SideMenu from "./components/SideMenu.vue"; //  引入左侧菜单组件
import BreadCrumb from "./components/BreadCrumb.vue"; //  引入面包屑导航栏
import FileTable from "./components/FileTable.vue"; //  引入文件表格展示区
import FilePagination from './components/FilePagination.vue' //  引入分页组件
import SelectColumn from './components/SelectColumn.vue' //  引入控制列显隐组件
import OperationMenu from './components/OperationMenu.vue' // 创建文件夹组件
import FileUploader from './components/FileUploader.vue' //  文件上传组件
import MoveFileDialog from './components/MoveFileDialog.vue' // 引入文件移动对话框组件
import ShowModel from './components/ShowModel.vue' // 引入图片展示方式组件
import FileGrid from './components/FileGrid.vue'
import FileTimeLine from './components/FileTimeLine'
import ImgReview from './components/ImgReview.vue'

import { getFileTree } from '../../request/file';
import { moveFile } from '@/request/file.js';
import { batchMoveFile } from '@/request/file.js'

export default {
  name: "Home",
  components: {
    SideMenu,
    BreadCrumb,
    FileTable,
    FilePagination,
    SelectColumn,
    OperationMenu,
    FileUploader,
    MoveFileDialog,
    ShowModel,
    FileGrid,
    FileTimeLine,
    ImgReview,
  },
  computed: {
    // 左侧菜单选中的文件类型
    fileType() {
      return this.$route.query.fileType  ? Number(this.$route.query.fileType) : 0
    },
    filePath() {
      return this.$route.query.filePath
    },
    showModel() {
      return this.$store.getters.showModel
    }
  },
  data() {
    return {
      loading: false,
      tableData: [],
      pageData: {
        currentPage: 1,
        pageCount: 20,
        total: 0
      },
      storageValue: 0,
      storageMaxValue: 0,
      // 移动文件模态框数据
      dialogMoveFile: {
        visible: false,
        fileTree: [],
      },
      isBatch: false, // 是否批量移动
      operationFile: {}, // 操作单个文件的信息
      operationFileList: [], // 多个文件信息
      selectFilePath: '',
    };
  },
  watch: {
    fileType() {
      this.getFileData();
    },
    filePath() {
      if(this.fileType === 0) {
        this.getFileData();
      }
    }
  },
  mounted() {
    this.getFileData(); //  获取文件列表
    this.getMaxStorageValue(); // 获取用户总空间大小
  },
  methods: {
    // 获取文件列表
    getFileData() {
      this.loading = true // 打开表格loading状态
      if (this.fileType === 0) {
        // 左侧菜单选择的为全部时，根据路径，获取文件列表
        this.loading = false
        this.getFileDataByPath()
      } else {
        // 左侧菜单选择的为 除全部以外的类型时，根据类型，获取文件列表
        this.getFileDataByType()
      }
      this.getStorageValue()
    },
    // 获取文件占用空间
    getStorageValue() {
      getFileStorage().then((res) => {
        if (res.success) {
          this.storageValue = res.data ? res.data : 0
        } else {
          this.$message.error(res.message)
        }
      })
    },
    // 获取用户总空间大小
    getMaxStorageValue() {
      getUserMaxStorage().then((res) => {
        if(res.success) {
          this.storageMaxValue = res.data ? res.data : 0;
          console.log(res.data);
        } else {
          this.$message.error(res.message);
        }
      })
    },
    // 根据路径获取文件列表
    getFileDataByPath() {
      getFileListByPath({
        filePath: this.filePath, // 传递当前路径
        currentPage: this.pageData.currentPage,
        pageCount: this.pageData.pageCount
      }).then(
        (res) => {
          this.loading = false //  关闭表格loading状态
          if (res.success) {
            this.tableData = res.data.list // 表格数据赋值
            this.pageData.total = res.data.total //  分页组件 - 文件总数赋值
          } else {
            this.$message.error(res.message)
          }
        },
        (error) => {
          this.loading = false
          console.log(error)
        }
      )
    },
    // 根据类型获取文件列表
    getFileDataByType() {
      getFileListByType({
        fileType: this.fileType, //  文件类型
        currentPage: this.pageData.currentPage, //  当前页码
        pageCount: this.pageData.pageCount //  每页条目数
      }).then(
        (res) => {
          this.loading = false //  关闭表格loading状态
          if (res.success) {
            this.tableData = res.data.list // 表格数据赋值
            this.pageData.total = res.data.total //  分页组件 - 文件总数赋值
          } else {
            this.$message.error(res.message)
          }
        },
        (error) => {
          this.loading = false
          console.log(error)
        }
      )
    },
    // 页码或当页条目数改变时
    changePageData(pageData) {
      this.pageData.currentPage = pageData.currentPage // 页码赋值
      this.pageData.pageCount = pageData.pageCount //  每页条目数赋值
      this.getFileData() // 获取文件列表
    },
    // 上传文件 按钮点击事件
    handleUploadFile() {
      //  触发子组件中的打开文件上传窗口事件
      this.$refs.globalUploader.triggerSelectFileClick()
    },
    // 设置移动文件时的文件信息
    setOperationFile(isBatch,file) {
      this.isBatch = isBatch;
      if(isBatch) {
        this.operationFileList = file;
      } else {
        this.operationFile = file;
      }
    },
    // 设置移动文件相关的对话框数据
    setMoveFileDialog(visible) {
      this.dialogMoveFile.visible = visible;
      if(visible) {
        getFileTree().then( res => {
          if(res.success) {
            this.dialogMoveFile.fileTree = [res.data];
          } else {
            this.$message.error(res.message);
          }
        });
      }
    },
    //  设置移动文件的目标路径
    setSelectFilePath(selectFilePath) {
      this.selectFilePath = selectFilePath
    },
    //  移动文件模态框-确定按钮事件
    confirmMoveFile() {
      if (this.isBatch) {
        //  批量移动
        let data = {
          filePath: this.selectFilePath,
          files: JSON.stringify(this.operationFileList)
        };
        batchMoveFile(data).then((res) => {
          if (res.success) {
            this.$message.success(res.data);
            this.getFileData() //  刷新文件列表
            this.dialogMoveFile.visible = false //  关闭对话框
            this.operationFileList = [];
          } else {
            this.$message.error(res.message);
          }
        })
      } else {
        //  单文件移动
        let data = {
          filePath: this.selectFilePath, //  目标路径
          oldFilePath: this.operationFile.filePath, //  原路径
          fileName: this.operationFile.fileName, //  文件名称
          extendName: this.operationFile.extendName //  文件扩展名
        }
        moveFile(data).then((res) => {
          if (res.success) {
            this.$message.success('移动文件成功')
            this.getFileData() //  刷新文件列表
            this.dialogMoveFile.visible = false //  关闭对话框
          } else {
            this.$message.error(res.message)
          }
        })
      }
    },
  }
};
</script>

<style lang="stylus" scoped>
.home {
  // 使用flex布局 菜单居左固定宽度 右侧内容区域自适应宽度
  display: flex;

  .home-left {
    box-sizing: border-box;
  }

  .home-right {
    box-sizing: border-box;
    width: calc(100% - 200px);
    padding: 8px 24px;
    flex: 1;
    .operation-wrapper {
      margin-bottom: 16px;
      display: flex;
      align-items: center;
      justify-content: space-between;

      // 左侧菜单按钮组 样式调整
    >>> .operation-menu-wrapper {
        flex: 1;
    }
    }
  }
}
</style>