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
      <el-button type="primary" @click="handleAdd">新增<i class="el-icon-circle-plus-outline"></i></el-button>
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
              @selection-change="handleSelectionChange">
      <el-table-column
          type="selection"
          width="55">
      </el-table-column>
      <el-table-column prop="id" label="id"></el-table-column>
      <el-table-column prop="name" label="名称"></el-table-column>
      <el-table-column prop="flag" label="唯一标识"></el-table-column>
      <el-table-column prop="description" label="描述"></el-table-column>

      <el-table-column label="操作" style="width: 200px">
        <template slot-scope="scope">
          <el-button type="info" class="el-icon-menu" slot="reference" @click="selectMenu(scope.row)">分配菜单
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

    <div class="block" style="padding: 10px 0">
      <span class="demonstration">完整功能</span>
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

    <el-dialog title="收货地址">
      <el-table>
        <el-table-column property="date" label="日期" width="150"></el-table-column>
        <el-table-column property="name" label="姓名" width="200"></el-table-column>
        <el-table-column property="address" label="地址"></el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog title="角色信息" :visible.sync="dialogFormVisible" width="30%">
      <el-form label-width="80px" size="small">
        <el-form-item label="角色">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="唯一标识">
          <el-input v-model="form.flag" autocomplete="off"></el-input>
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

    <el-dialog title="菜单分配" :visible.sync="menuDialogVisible" width="30%">
      <el-tree
          :data="menuData"
          :props="props"
          node-key="id"
          show-checkbox
          ref="tree"
          :default-expanded-keys="expends"
          :default-checked-keys="checks"
          @check-change="handleCheckChange"
      >
     <span slot-scope="{node,data}" :class="custom-tree-node">
        <span><i :class="data.icon"></i>{{ data.name }}</span>
     </span>
      </el-tree>

      <div slot="footer" class="dialog-footer">
        <el-button @click="menuDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveRoleMenu">确 定</el-button>
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
      menuDialogVisible: false,
      menuData: [],
      props: {
        label: 'name'
      },
      expends: [],
      checks: [],
      roleId: 0,
      roleFlag: "",
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
    /* 有些后端习惯统一用 POST 做所有增删改查（不遵守 REST 语义），哪怕是删除操作，后端也只写 @PostMapping，
    这种场景前端就不能用 delete 方法。*/
    batchDelete() {
      let ids = this.multipleSelection.map(v => v.id)
      console.log("选中ids：", ids)
      this.request.post("/role/del/batch", ids).then(res => {
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
      this.form = row
      this.dialogFormVisible = true
    },
    handleDelete(id) {
      this.request.delete("/role/" + id).then(res => {
        if (res.code == "200") {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      })
    },
    save() {
      this.request.post("/role", this.form).then(res => {
        console.log(res)
        if (res.code == "200") {
          this.$message.success("保存成功")
          this.dialogFormVisible = false
          this.load()
        } else {
          this.$message.error("ERROR")
        }

      })
    },
    handleAdd() {
      this.dialogFormVisible = true
      this.form = {}

    },
    reset() {
      this.name = ""
      this.load()
    },
    load() {
      this.request.get("/role/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {
        console.log(res)
        this.tableData = res.data.records
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
    selectMenu(role) {
      this.roleId = role.id
      this.roleFlag = role.flag
      this.menuDialogVisible = true
      //请求菜单数据
      this.request.get("/menu").then(res => {
        //console.log(res)
        this.menuData = res.data
        //把后台返回的菜单数据处理成id数组
        this.expends = this.menuData.map(v => v.id)
        //菜单树加载完，再查已勾选
        //请求
        this.request.get("/role/roleMenu/" + this.roleId).then(res => {
          //console.log(res)
          this.checks = res.data

          this.request.get("/menu/ids").then(r => {
            //先渲染弹窗里的元素
            this.menuDialogVisible = true
            //console.log(res)
            const ids = r.data;
            ids.forEach(id => {
              if (!this.checks.includes(id))
                //this.$nextTick
                this.$refs.tree.setChecked(id, false)
            })
          })
        })
      })


    },
    handleCheckChange(data, checked, indeterminate) {
      console.log(data, checked, indeterminate)
    },
    /*    saveRoleMenu(){
          //console.log(this.$refs.tree.getCheckedNodes())
          //console.log(this.$refs.tree.getCheckedKeys())
          this.request.post("role/roleMenu/"+this.roleId,this.$refs.tree.getCheckedKeys()).then(res=>{
            //console.log(res)
            if (res.code=="200"){
              this.$message.success("分配成功")
              this.menuDialogVisible=false
            }else{
              this.$message.error(res.msg)
            }
          })

        }*/
    saveRoleMenu() {
      const checkedKeys = this.$refs.tree.getCheckedKeys()
      const halfKeys = this.$refs.tree.getHalfCheckedKeys()
      //合并：选中的子菜单 + 半选父菜单
      const allIds = [...checkedKeys, ...halfKeys]

      this.request.post("role/roleMenu/" + this.roleId, allIds).then(res => {
        if (res.code == "200") {
          this.$message.success("分配成功")
          this.menuDialogVisible = false
          //操作管理员的时候，需要重新登录一下
          if (this.roleFlag == "ROLE_ADMIN") {
            //退出,请求到store文件夹下的index.js
            this.$store.commit("logout")
          }

        } else {
          this.$message.error(res.msg)
        }
      })
    }

  }
}
</script>
<style>
.headerBg {
  background-color: #eee !important;
}
</style>