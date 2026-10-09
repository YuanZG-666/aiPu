<template>
  <div style="line-height: 60px;display: flex">
    <div style="flex: 1">
      <span :class="collapseBtnClass" style="cursor: pointer;font-size:18px" @click="toggleCollapse"></span>
      <el-breadcrumb separator="/" style="display: inline-block;margin-left: 10px">
        <el-breadcrumb-item :to="{path:'/'}">首页</el-breadcrumb-item>
        <el-breadcrumb-item >{{currentPathName}}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <el-dropdown style="width: 150px;cursor: pointer;text-align: right">
      <div style="display: inline-block">
        <img :src="user.avatar" alt="" style="width: 30px;border-radius: 50%;position: relative;top: 10px;right: 5px">
        <span>{{user.nickname}}</span><i class="el-icon-arrow-down" style="margin-left: 5px"></i>
      </div>

      <el-dropdown-menu slot="dropdown" style="width: 100px;text-align: center">
        <el-dropdown-item style="font-size: 14px;padding: 5px 0">
          <router-link to="/person" style="text-decoration: none;color: #606266">
            个人信息
          </router-link>
        </el-dropdown-item>
        <el-dropdown-item style="font-size: 14px;padding: 5px 0">
          <span style="text-decoration: none;color: #606266" @click="logout">退出</span>
        </el-dropdown-item>

      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>

<script>
export default {
  name:"Header",
  props:{
    collapseBtnClass:String,
    collapse:Boolean,
    user:Object

  },
  computed:{
    currentPathName(){
      return this.$store.state.currentPathName
    }
  },
  watch:{//监听路由变化
    '$route':function (){
      this.currentPathName = localStorage.getItem("currentPathName")
    }
  },
  data() {
    return {
    }

  },
  methods: {
    toggleCollapse() {
      // 注释里这种连续$parent写法是不推荐的！耦合太高，组件层级一变直接崩
      // this.$parent.$parent.$parent.$parent.collapse()

      // 子组件触发自定义事件，通知父组件：要折叠侧边栏
      this.$emit("asideCollapse")//子组件触发自定义事件，通知父组件：要折叠侧边栏
    },
    logout() {
      this.$confirm('此操作将退出登录, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$router.push('/login')
        localStorage.removeItem("user")
        localStorage.removeItem("menus")
        localStorage.removeItem("token") //新增
        this.$message({
          type: 'success',
          message: '退出登录成功!'
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消退出登录'
        });
      });
    }
  },


}

</script>
<style scoped>

</style>