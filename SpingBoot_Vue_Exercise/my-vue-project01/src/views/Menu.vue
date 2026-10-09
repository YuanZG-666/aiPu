<template>
  <div>

    <div style="margin: 10px 0">
      <el-input style="width: 200px" placeholder="请输入名称" suffix-icon="el-icon-search" type="text"
                v-model="name"></el-input>
      <!--      <el-input style="width: 200px" placeholder="请输入邮箱" class="ml-5" suffix-icon="el-icon-message" type="text"
                      v-model="email"></el-input>
            <el-input style="width: 200px" placeholder="请输入地址" class="ml-5" suffix-icon="el-icon-position"
                      type="text" v-model="address"></el-input>-->
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>

    <div style="margin: 10px 0">
      <el-button type="primary" @click="handleAdd()">新增<i class="el-icon-circle-plus-outline"></i></el-button>
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
      <!--      <el-upload action="http://localhost:9090/role/import" show-file-list=false accept="'xls,xlsx'" :on-success="handleImportExcelSuccess" style="display: inline-block">
              <el-button type="primary" class="ml-5">导入<i class="el-icon-upload"></i></el-button>
            </el-upload>
            <el-button type="primary" @click="expo" class="ml-5">导出<i class="el-icon-download"></i></el-button>-->
    </div>

    <el-table :data="tableData" border stripe header-cell-class-name="headerBg"
              @selection-change="handleSelectionChange"
              row-key="id" default-expand-all>
      <el-table-column
          type="selection"
          width="55">
      </el-table-column>
      <el-table-column prop="id" label="id"></el-table-column>
      <el-table-column prop="name" label="名称"></el-table-column>
      <el-table-column prop="path" label="路径"></el-table-column>
      <el-table-column prop="pagePath" label="页面路径"></el-table-column>
      <el-table-column  label="图标" class-name="fontSize18" align="center" label-class-name="fontSize12">
        <template slot-scope="scope">
          <i :class="scope.row.icon" />
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述"></el-table-column>
      <el-table-column label="操作" width="300" align="center">
        <template slot-scope="scope">
          <el-button type="primary" class="el-icon-plus" slot="reference" v-if="!scope.row.pid && !scope.row.path"
                     @click="handleAdd(scope.row.id)">新增子菜单
          </el-button>
          <el-button type="primary" class="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
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


    <el-dialog title="收货地址">
      <el-table>
        <el-table-column property="date" label="日期" width="150"></el-table-column>
        <el-table-column property="name" label="姓名" width="200"></el-table-column>
        <el-table-column property="address" label="地址"></el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog title="菜单信息" :visible.sync="dialogFormVisible" width="30%">
      <el-form label-width="80px" size="small">
        <el-form-item label="名称">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="路径">
          <el-input v-model="form.path" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="页面路径">
          <el-input v-model="form.pagePath" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="图标">
          <!--          <el-input v-model="form.icon" autocomplete="off"></el-input>-->
          <el-select clearable v-model="form.icon" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in options" :key="item.name" :label="item.name" :value="item.value">
              <i :class="item.value" />{{item.name}}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
export default {
  name: "User",
  data() {
    return {
      total: 0,
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      name: "",
      form: {},
      multipleSelection: [],
      headerBg: 'headerBg',
      dialogFormVisible: false,
      options: []
    }
  },
  created() {
    this.load()
  },
  methods: {
    handleSelectionChange(val) {
      console.log(val)
      this.multipleSelection = val
    },
    batchDelete() {
      let ids = this.multipleSelection.map(v => v.id)
      console.log("选中ids：", ids)
      this.request.post("/menu/del/batch", ids).then(res => {
        if (res.code == "200") {
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
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
      //请求图标的数据
      this.request.get("/menu/icons").then(res => {
        this.options = res.data
      })

    },
    handleDelete(id) {
      this.request.delete("/menu/" + id).then(res => {
        if (res.code == "200") {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      })
    },
    save() {
      console.log("提交的表单数据：",this.form) // 新增这一行
      this.request.post("/menu", this.form).then(res => {
        console.log(res)
        if (res.code == "200") {
          this.$message.success("保存成功")
          this.dialogFormVisible = false
          this.load()
        } else {
          this.$message.error("ERROR")
        }
      }).catch(err=>{
        console.log("请求失败详情",err.response.data) // 捕获400后端返回信息
      })
    },
    handleAdd(pid) {
      this.dialogFormVisible = true
      this.form = {}
      if (pid && !(pid instanceof Event)) {
        this.form.pid = pid
      }
      // =========新增这一段：新增的时候也加载图标列表=========
      this.request.get("/menu/icons").then(res => {
        this.options = res.data
      })
    },
    reset() {
      this.name = ""
      this.load()
    },
    load() {
      this.request.get("/menu", {
        params: {
          name: this.name,
        }
      }).then(res => {
        console.log(res),
            this.tableData = res.data
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
    expo() {
      console.log('点击导出触发了');
      window.open("http://localhost:9090/menu/export")
    },
    handleImportExcelSuccess() {
      this.$message.success("导入成功")
      this.load()
    }
  }
}
</script>
<style>
.headerBg {
  background-color: #eee !important;
}
.fontSize12{
  font-size: 12px;
}
.fontSize18{
  font-size: 18px;
}
</style>