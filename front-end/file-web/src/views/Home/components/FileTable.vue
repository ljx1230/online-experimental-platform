<template>
  <el-table class="file-table" :data="tableData" height="calc(100vh - 202px)" style="width: 100%"  v-loading="loading" @selection-change="handleSelectRow">
     <!-- 勾选框 -->
     <el-table-column type="selection" width="56" align="center"></el-table-column>
    <el-table-column label prop="isDir" width="60" align="center">
      <template slot-scope="scope">
        <img :src="setFileImg(scope.row)" style="width: 30px" />
      </template>
    </el-table-column>
    <el-table-column prop="fileName" label="文件名">
      <template slot-scope="scope">
        <div style="cursor: pointer" @click="handleFileNameClick(scope.row)">
            {{ scope.row.extendName ? `${scope.row.fileName}.${scope.row.extendName}` : `${scope.row.fileName}` }}
        </div>
      </template>
    </el-table-column>
    <el-table-column
      label="所在路径"
      prop="filePath"
      show-overflow-tooltip
      v-if="fileType !== 0"
    >
      <template slot-scope="scope">
        <div
          style="cursor: pointer"
          title="点击跳转"
          @click="
            $router.push({
              query: { fileType: 0, filePath: scope.row.filePath }
            })">{{ scope.row.filePath }}
        </div>
      </template>
    </el-table-column>
    <el-table-column prop="extendName" label="类型" width="100" v-if="selectedColumnList.includes('extendName')">
      <template slot-scope="scope">
        <span>{{ scope.row.extendName ? scope.row.extendName : '文件夹' }}</span>
      </template>
    </el-table-column>
    <el-table-column prop="fileSize" label="大小" width="60"  v-if="selectedColumnList.includes('fileSize')">
      <template slot-scope="scope">
          <span>{{ calculateFileSize(scope.row.fileSize) }}</span>
      </template>
    </el-table-column>
    <el-table-column prop="uploadTime" label="修改日期" width="180" v-if="selectedColumnList.includes('uploadTime')"></el-table-column>
    <el-table-column :width="operaColumnIsFold ? 200 : 100">
      <!-- 自定义表格头 -->
      <template slot="header">
        <span>操作</span>
        <i
          class="el-icon-circle-plus"
          title="展开"
          @click="operaColumnIsFold = true"
        ></i>
        <i
          class="el-icon-remove"
          title="折叠"
          @click="operaColumnIsFold = false"
        ></i>
      </template>
      <template slot-scope="scope">
        <!-- 操作列展开状态下的按钮 -->
        <div class="opera-unfold" v-if="operaColumnIsFold">
          <el-button
            type="text"
            size="small"
            @click.native="handleClickDelete(scope.row)"
            >删除</el-button
          >
          <el-button
            type="text"
            size="small"
            @click.native="handleClickMove(scope.row)"
            >移动</el-button
          >
          <el-button
            type="text"
            size="small"
            @click.native="handleClickRename(scope.row)"
            >重命名</el-button
          >
          <el-button type="text" size="small" v-if="scope.row.isDir === 0">
            <a target="_blank" style="display: block; color: inherit" :href="`/api/filetransfer/downloadfile?userFileId=${scope.row.userFileId}`">下载</a>
          </el-button>
        </div>
        <el-dropdown trigger="click" v-else>
          <el-button size="mini">
            操作
            <i class="el-icon-arrow-down el-icon--right"></i>
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item @click.native="handleClickDelete(scope.row)"
              >删除</el-dropdown-item
            >
            <el-dropdown-item @click.native="handleClickMove(scope.row)"
              >移动</el-dropdown-item
            >
            <el-dropdown-item @click.native="handleClickRename(scope.row)"
              >重命名</el-dropdown-item
            >
            <el-dropdown-item v-if="scope.row.isDir === 0">
              <a :href="`/api/filetransfer/downloadfile?userFileId=${scope.row.userFileId}`" target="_blank" style="display: block; color: inherit">下载</a>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
import { deleteFile } from "../../../request/file.js";
import { renameFile } from '@/request/file.js' //  文件重命名
export default {
  name: "FileTable",
  props: {
    // 表格数据，同时需要删除原本在 data( return { } ) 中的 tableData，否则会报错
    tableData: {
      type: Array,
      required: true
    },
    // 表格加载状态
    loading: {
      type: Boolean,
      required: true
    },
    // 文件类型
    fileType: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      //  文件图片Map映射
      fileImgMap: {
        apk: require('@/assets/image/apk.png'),
        csv: require('@/assets/image/csv.png'),
        doc: require('@/assets/image/doc.png'),
        excel: require('@/assets/image/excel.png'),
        exe: require('@/assets/image/exe.png'),
        fold: require('@/assets/image/fold.png'),
        gif: require('@/assets/image/gif.png'),
        html: require('@/assets/image/html.png'),
        json: require('@/assets/image/json.png'),
        mp3: require('@/assets/image/mp3.png'),
        mp4: require('@/assets/image/mp4.png'),
        other: require('@/assets/image/other.png'),
        pdf: require('@/assets/image/pdf.png'),
        ppt: require('@/assets/image/ppt.png'),
        rar: require('@/assets/image/rar.png'),
        svg: require('@/assets/image/svg.png'),
        txt: require('@/assets/image/txt.png'),
        zip: require('@/assets/image/zip.png')
      },
      operaColumnIsFold: sessionStorage.getItem('operaColumnIsFold') || false // 表格操作列-是否收缩
    };
  },
  computed: {
    // 表格显示列
    selectedColumnList() {
      return this.$store.getters.selectedColumnList
    }
  },
  methods: {
    //  计算文件大小
    calculateFileSize(size) {
        const B = 1024
        const KB = Math.pow(1024, 2)
        const MB = Math.pow(1024, 3)
        const GB = Math.pow(1024, 4)
        if (!size) {
            return '_'
        } else if (size < KB) {
            return (size / B).toFixed(0) + 'KB'
        } else if (size < MB) {
            return (size / KB).toFixed(1) + 'MB'
        } else if (size < GB) {
            return (size / MB).toFixed(2) + 'GB'
        } else {
            return (size / GB).toFixed(3) + 'TB'
        }
    },
    // 删除按钮 - 点击事件
    handleClickDelete(row) {
      // console.log("删除", row.fileName);
      this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          // 确定按钮,调用删除文件接口
          deleteFile(row).then((res) => {
            if (res.success) {
              this.$emit('getTableData') //  刷新文件列表
              this.$message.success('删除成功')
            } else {
              this.$message.error(res.message)
            }
          })
        })
        .catch(() => {
          //  取消
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    // 移动按钮 - 点击事件
    handleClickMove(row) {
      // console.log("移动", row.fileName);
      this.$emit('handleSelectFile', false, row) // true/false 操作类型：批量移动/单文件操作；row 当前行文件数据
      this.$emit('handleMoveFile', true) // true/false 打开/关闭移动文件对话框
    },
    // 重命名按钮 - 点击事件
    handleClickRename(row) {
      let fileName = row.fileName
      this.$prompt('请输入文件名', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValue: fileName,
        inputPattern: /\S/, //  文件名不能为空
        inputErrorMessage: '请输入文件名',
        closeOnClickModal: false
      })
        .then(({ value }) => {
          // 确定按钮 调用重命名接口
          renameFile({
            ...row,
            fileName: value
          }).then((res) => {
            if (res.success) {
              this.$emit('getTableData') //  刷新文件列表
              this.$message.success('文件已重命名')
            } else {
              this.$message.error(res.message)
            }
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '取消'
          })
        })
    },
    // 文件名点击事件
    handleFileNameClick(row) {
        //  若是目录则进入目录
        if (row.isDir) {
            this.$router.push({
                query: {
                    filePath: `${row.filePath}${row.fileName}/`,
                    fileType: 0
                }
            })
        } else {
          //  若当前点击项是图片
        const PIC = ['png', 'jpg', 'jpeg', 'gif', 'svg']
        if (PIC.includes(row.extendName)) {
            let data = {
                imgReviewVisible: true,
                imgReviewList: [{
                    fileUrl: `/api${row.fileUrl}`,
                    downloadLink: `/api/filetransfer/downloadfile?userFileId=${row.userFileId}`,
                    fileName: row.fileName,
                    extendName: row.extendName
                }],
                activeIndex: 0
            };
            this.$store.commit('setImgReviewData', data);
        }
        }
    },
    //  根据文件扩展名设置文件图片
    setFileImg(row) {
      if (!row.extendName) {
        //  文件夹
        return this.fileImgMap.fold
      } else if (['jpg', 'png', 'jpeg'].includes(row.extendName)) {
        // 图片类型，直接显示缩略图
        return this.downloadImgMin(row)
      } else {
        const fileTypeMap = {
          pptx: 'ppt',
          doc: 'word',
          docx: 'doc',
          xls: 'excel',
          xlsx: 'excel'
        }
        //  可以识别文件类型的文件
        return this.fileImgMap[row.extendName] || this.fileImgMap[fileTypeMap[row.extendName]] || this.fileImgMap.other
      }
    },
    // 表格行勾选事件
    handleSelectRow(selection) {
      this.$emit('handleSelectFile', true, selection) // true/false 批量移动/单文件操作；selection 勾选的表格行数据
    },
  },
  watch: {
    // 监听收缩状态变化，存储在sessionStorage中，保证页面刷新时仍然保存设置的状态
    operaColumnIsFold(newValue) {
      sessionStorage.setItem('operaColumnIsFold', newValue)
    }
  },
  created() {
    this.operaColumnIsFold = sessionStorage.getItem('operaColumnIsFold') === 'true' //  读取保存的状态
  },
};
</script>

<style lang="stylus" scoped>
// 表格操作列-表头图标样式调整
.el-icon-circle-plus, .el-icon-remove {
  margin-left: 8px;
  cursor: pointer;
  font-size: 16px;

  &:hover {
    opacity: 0.5;
  }
}
.file-table {
  // 调整滚动条样式
  >>> .el-table__body-wrapper {
    setScrollbar(8px, #EBEEF5, #909399);
  }
}
</style>