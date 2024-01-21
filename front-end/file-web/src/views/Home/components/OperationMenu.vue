<template>
  <div class="operation-menu-wrapper">
    <!-- 按钮组 -->
    <el-button-group class="operate-group">
      <!-- disabled 只在全部类型页面 才可新建文件夹 -->
      <el-button
        size="mini"
        type="primary"
        icon="el-icon-plus"
        :disabled="fileType !== 0"
        @click="addFolderDialog.visible = true">新建文件夹</el-button>
      <el-button type="primary" size="mini" icon="el-icon-upload2" @click="handleUploadFileClick()" :disabled="fileType !== 0">上传文件</el-button>
      <el-button
        size="mini"
        type="primary"
        icon="el-icon-delete"
        :disabled="!operationFileList.length"
        @click="handleDeleteFileClick()"
        >批量删除</el-button>
      <el-button
        size="mini"
        type="primary"
        icon="el-icon-rank"
        :disabled="!operationFileList.length"
        v-if="fileType === 0"
        @click="handleMoveFileClick()"
        >批量移动</el-button>
        <el-button
        size="mini"
        type="primary"
        icon="el-icon-rank"
        :disabled="!operationFileList.length"
        v-if="fileType === 0"
        @click="handleDownloadFileClick()"
        >批量下载</el-button>
    </el-button-group>

    <!-- 对话框 - 新建文件夹 -->
    <!-- @closed 对话框关闭动画结束时 重置表单并清空所有表单校验 -->
    <el-dialog
      title="新建文件夹"
      width="600px"
      :visible.sync="addFolderDialog.visible"
      @closed="$refs.addFolderForm.resetFields()"
    >
      <el-form
        :model="addFolderForm"
        :rules="addFolderRules"
        label-position="top"
        ref="addFolderForm"
      >
        <el-form-item label="文件夹名称" prop="name">
          <el-input v-model="addFolderForm.name"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addFolderDialog.visible = false">取 消</el-button>
        <el-button
          type="primary"
          :loading="addFolderDialog.loading"
          @click="handleAddFolderSubmit('addFolderForm')"
          >提 交</el-button
        >
      </span>
    </el-dialog>
    <a
      v-for="(item, index) in operationFileList"
      :key="index"
      :href="`/api/filetransfer/downloadfile?userFileId=${item.userFileId}`"
      :download="`${item.fileName}.${item.extendName}`"
      :ref="`downloadLink${index}`"
    ></a>
  </div>
</template>

<script>
import { createFile } from "@/request/file.js";
import { batchDeleteFile } from '@/request/file.js'

export default {
  name: "OperationMenu",
  props: {
    // 文件类型
    fileType: {
      type: Number,
      required: true,
    },
    // 文件路径
    filePath: {
      type: String,
      required: true,
    },
    operationFileList: {
      type: Array,
      required: true,
    },
  },
  data() {
    return {
      // 新建文件夹对话框数据
      addFolderDialog: {
        visible: false, //  对话框显隐状态
        loading: false,
      },
      // 新建文件夹表单
      addFolderForm: {
        name: "",
      },
      // 新建文件夹表单校验规则
      addFolderRules: {
        name: [
          {
            required: true,
            message: "请输入文件夹名称",
            trigger: "blur, change",
          },
        ],
      },
    };
  },
  methods: {
    // 新建文件夹对话框 - 提交按钮
    handleAddFolderSubmit(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.addFolderDialog.loading = true; //  对话框的确定按钮打开loading状态
          // 表单校验通过 - 调用新建文件夹接口
          createFile({
            fileName: this.addFolderForm.name,
            filePath: this.filePath, //  文件路径
            isDir: 1, //  是否为文件夹：1 表示文件夹 0 表示文件
          }).then(
            (res) => {
              this.addFolderDialog.loading = false; //  对话框的确定按钮关闭loading状态
              if (res.success) {
                this.$message.success("添加成功");
                this.addFolderDialog.visible = false; //  关闭对话框
                this.$emit("getTableData"); // 重新获取文件列表
              } else {
                this.$message.warning(res.message);
              }
            },
            (error) => {
              this.addFolderDialog.loading = false; //  对话框的确定按钮关闭loading状态
              console.log(error);
            }
          );
        } else {
          return false;
        }
      });
    },
    // 上传文件按钮
    handleUploadFileClick() {
      this.$emit('handleUploadFile', true)
    },
    handleDeleteFileClick() {
      this.$confirm('此操作将永久删除文件，是否继续？','提示',{
        confirmButtonText: "确定",
        cancelButtonText: "取消"
      }).then(() => {
        // 点击确定
         batchDeleteFile({
          files: JSON.stringify(this.operationFileList)
         }).then(res => {
          if(res.success) {
            this.$message({
              message: res.message,
              type: 'success'
            });
            this.$emit('getTableData'); //  刷新文件列表
          } else {
            this.$message.error('失败' + res.message);
          }
         }) .catch(() => {
          // 取消
          this.$message({
            type:'info',
            message: "已取消删除"
          })
         });
      });
    },
    // 移动文件按钮
    handleMoveFileClick() {
        // true/false 批量移动/单文件操作 | this.operationFileList 当前行文件数据
        this.$emit('handleSelectFile', true, this.operationFileList)
        this.$emit('handleMoveFile', true) // true/false 打开/关闭移动文件对话框
    },
    // 批量下载文件按钮
    handleDownloadFileClick() {
      for(let i = 0;i < this.operationFileList.length;i++) {
        this.$refs[`downloadLink${i}`][0].click();
      }
    }
  },
};
</script>