<template>
  <div>
    <div style="margin: 10px 0">
      <el-input style="width: 200px" placeholder="请输入名称" suffix-icon="el-icon-search" type="text"
                v-model="name"></el-input>
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>
    <div style="margin: 10px 0">
      <el-upload action="http://localhost:9090/file/upload" show-file-list=false :on-success="handleFileUploadSuccess" style="display: inline-block">
        <el-button type="primary" class="ml-5">上传文件<i class="el-icon-top"></i></el-button>
      </el-upload>
      <el-popconfirm
          class="ml-5"
          confirm-button-text='确定'
          cancel-button-text='取消'
          icon="el-icon-info"
          icon-color="red"
          title="确定要批量删除数据吗？"
          @confirm="batchDelete"
      >
        <el-button type="primary" slot="reference">批量删除<i class="el-icon-delete"></i></el-button>
      </el-popconfirm>

    </div>
    <el-table :data="tableData" border stripe header-cell-class-name="headerBg"
              @selection-change="handleSelectionChange">
      <el-table-column
          type="selection"
          width="55">
      </el-table-column>
      <el-table-column prop="id" label="id" ></el-table-column>
      <el-table-column prop="name" label="文件名称" ></el-table-column>
      <el-table-column prop="type" label="文件类型" ></el-table-column>
      <el-table-column prop="size" label="文件大小（kb）"></el-table-column>
      <el-table-column prop="url" label="下载链接">
        <template slot-scope="scope">
          <el-button type="primary" @click="downloadFile(scope.row.url)">下载</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="enable" label="启用">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.enable" active-color="#13ce66" inactive-color="#ccc" @change="changeEnable(scope.row)"></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-popconfirm
              class="ml-5"
              confirm-button-text='确定'
              cancel-button-text='取消'
              icon="el-icon-info"
              icon-color="red"
              title="确定要删除吗？"
              @confirm="handleDelete(scope.row.id)"
          >
            <el-button type="danger" class="el-icon-remove-outline" slot="reference">删除</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[2, 5, 10, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
    </el-pagination>
  </div>
</template>
<script>
export default {
  name: 'File',
  data(){
    return{
      tableData: [],
      name:'',
      multipleSelection: [],
      pageNum:1,
      pageSize:5,
      total:0
    }
  },
  created() {
    this.load()
  },
  methods:{
    handleSelectionChange(val) {
      console.log(val)
      this.multipleSelection = val
    },
    batchDelete() {
      let ids = this.multipleSelection.map(v => v.id)
      console.log("选中ids：", ids)
      this.request.post("/file/del/batch", ids).then(res => {
        if (res.code=="200") {
          this.$message.success("批量删除成功")
          this.load()
        } else {
          this.$message.error("批量删除失败")
        }
      })
          .catch(err => {
            console.error(err)
            this.$message.error("请求异常")
          })
    },

    handleDelete(id) {
      this.request.delete("/file/" + id).then(res => {
        if (res.code=="200") {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      })
    },
    reset() {
      this.name = "",
      this.load()
    },
    load() {
      this.request.get("/file/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {
        console.log(res),
            this.tableData = res.data.records,
            this.total = res.data.total
      })

    },
    handleSizeChange(pageSize) {
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum
      this.load()
    },
    handleFileUploadSuccess(res){
      console.log(res)
      this.load()
    },
    downloadFile(url){
      window.open(url)
    },
    changeEnable(row){
      this.request.post("/file/update", row).then(res => {
        if (res.code=="200") {
          this.$message.success("操作成功")
        } else {
          this.$message.error("修改失败")
        }
      })
    }
  }
}
</script>

<style scoped>

</style>