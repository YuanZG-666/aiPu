<template>
  <el-menu :default-openeds="opens" style="min-height: 100%;overflow-x: hidden"
           background-color="rgb(48,65,86)" text-color="#fff"
           active-text-color="#ffd04b"
           :collapse-transition="false"
           :collapse="isCollapse"
           router
           @select="handleSelect"
  >

    <div style="height: 60px;line-height: 60px;text-align: center">
      <img style="width: 20px;position: relative;top: 5px;margin-left: 5px" src="../assets/logo.png">
      <b style="color: white" v-show="logoTextShow">后台管理系统</b>
    </div>

    <div v-for="item in menus" :key="item.id">
      <div v-if="item.path">
        <el-menu-item :index="item.path">
<!--          <template slot="title">-->
            <i :class="item.icon" v-show="true"></i>
            <span>{{ item.name }}</span>
<!--          </template>-->
        </el-menu-item>
      </div>

      <div v-else v-if="item.children && item.children.length">
        <el-submenu :index="item.id">
          <template slot="title">
            <i :class="item.icon" v-show="true"></i>
            <span>{{ item.name }}</span>
          </template>
          <div v-for="subItem in item.children" :key="subItem.id">
            <el-menu-item :index="subItem.path">
              <template slot="title">
                <i :class="subItem.icon" v-show="true"></i>
                <span>{{ subItem.name }}</span>
              </template>
            </el-menu-item>
          </div>
        </el-submenu>
      </div>
    </div>


    <!--          <el-menu-item index="/home" >
                <template slot="title">
                  <i class="el-icon-s-home" v-show="true"></i>
                  <span slot="title">主页</span>
                </template>
              </el-menu-item>

        <el-submenu index="2">
          <template slot="title">
            <i class="el-icon-menu" v-show="true"></i>
            <span slot="title">系统管理</span>
          </template>
          <el-menu-item index="/user">
            <i class="el-icon-s-custom"></i>
            <span slot="title">用户管理</span>
          </el-menu-item>
          <el-menu-item index="/role">
            <i class="el-icon-s-custom"></i>
            <span slot="title">角色管理</span>
          </el-menu-item>
          <el-menu-item index="/menu">
            <i class="el-icon-s-custom"></i>
            <span slot="title">菜单管理</span>
          </el-menu-item>
          <el-menu-item index="/file">
            <i class="el-icon-document"></i>
            <span slot="title">文件管理</span>
          </el-menu-item>
        </el-submenu>-->

  </el-menu>
</template>

<script>
export default {
  name: "Aside",
  props: {
    isCollapse: Boolean,
    logoTextShow: Boolean
  },
  data() {
    return {
      menus: localStorage.getItem("menus") ? JSON.parse(localStorage.getItem("menus")) : [],
      opens: localStorage.getItem("menus") ? JSON.parse(localStorage.getItem("menus")).map(v=>v.id) : []
    }
  },
  methods: {
    handleSelect(index) {
      //console.log(index)
      // console.log(this.$route)
      //console.log(this.$router.options.routes)

    }
  }
}
</script>

<style scoped>

</style>