<template>

  <el-container style="height:100vh">

    <el-aside :width="sideWith + 'px'" style="background-color: rgb(238, 241, 246);height:100%">
      <Aside :isCollapse="isCollapse" :logoTextShow="logoTextShow" ></Aside>
    </el-aside>

    <el-container>

      <el-header style=" border-bottom: 1px solid #ccc">
        <Header :collapseBtnClass="collapseBtnClass" :collapse="isCollapse" :user="user" @asideCollapse="handleCollapse"></Header>
      </el-header>

      <el-main>
<!--        表示当前页面的子路由会在<router-view />里面展示-->
        <router-view @refreshUser = "getUser"/>
      </el-main>

    </el-container>
  </el-container>


</template>

<script>
// @ is an alias to /src
import HelloWorld from '@/components/HelloWorld.vue'
import request from "@/utils/request";
import Aside from "@/components/Aside.vue";
import Header from "@/components/Header.vue";



export default {
  name: 'HomeView',
  components: {
    Header,
    Aside,
    HelloWorld
  },
  data() {
    return {
      collapseBtnClass: 'el-icon-s-fold',
      isCollapse: false,
      sideWith: 200,
      logoTextShow: true,
      user: {}

    }
  },
  created() {
    this.getUser()
  },
  methods: {
    handleCollapse(){
      this.isCollapse = !this.isCollapse;
      if (this.isCollapse) {
        this.sideWith = 64
        this.collapseBtnClass = 'el-icon-s-unfold'
        this.logoTextShow = false
      } else {
        this.sideWith = 200
        this.collapseBtnClass = 'el-icon-s-fold'
        this.logoTextShow = true
      }
    },
    getUser(){
      let username =localStorage.getItem("user")?JSON.parse(localStorage.getItem('user')).username:""
      //从后台获取User数据
      this.request.get("/user/username/"+username).then(res=>{
        //重新赋值User数据
        this.user=res.data
      })
    },
  }
}
</script>


