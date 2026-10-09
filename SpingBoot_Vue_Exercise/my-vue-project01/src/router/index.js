import Vue from 'vue'
import VueRouter from 'vue-router'
import Manage from '../views/Manage.vue'
import store from "@/store";

Vue.use(VueRouter)



const routes = [
  {
    path: '/about',
    name: 'about',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/404',
    name: '404',
    component: () => import('../views/404.vue')
  },
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})


router.beforeEach((to, from, next) => {
  localStorage.setItem("currentPathName",to.name)
  store.commit("setPath")//触发store的数据更新
  // 未找到路由的情况
  if (!to.matched.length) {
    // to.matched：当前路由匹配到的路由数组，length=0 说明没有匹配到任何路由，也就是访问的地址不存在
    const storeMenus = localStorage.getItem("menus")
    // 从浏览器本地存储 localStorage 读取key为menus的值（后端返回的菜单列表，登录之后才会存入）
    if (storeMenus) {
      // 有菜单数据，说明用户已经登录，只是访问了不存在的地址 → 跳404
      next({ path: "/404" })
    } else {
      // 没有菜单数据 = 用户未登录，直接跳登录页
      next({ path: "/login" })
    }
  }
  next()
// 路由放行，正常走路由跳转



  //console.log(from)
/*  localStorage.setItem("currentPathName",to.name)
  store.commit("setPath")//触发store的数据更新

  const storeMenus =localStorage.getItem("menus")*/



// 白名单：不需要登录校验的页面
/*
  const whiteList = ['login', 'register', 'about']
  // 如果目标页面在白名单，直接放行，不再走权限判断
  if (whiteList.includes(to.name)) {
    localStorage.setItem("currentPathName",to.name)
    store.commit("setPath")
    next()
    return
  }

  const storeMenus = localStorage.getItem("menus")
  if (storeMenus) {
    localStorage.setItem("currentPathName",to.name)
    store.commit("setPath")
    next()
  } else {
    next("/login")
  }
*/

/*  if (storeMenus){
    next()
  }else {
    next("/login")
  }*/

/*  // 白名单：不需要登录就能访问的页面
  const whiteList = ['login','register','about']
  // 如果去往的页面在白名单，直接放行
  if(whiteList.includes(to.name)){
    next()
    return
  }

  // 有菜单并且有token，放行；否则跳login
  if (storeMenus && token){
    next()
  }else {
    next("/login")
  }*/
})

//重置路由方法
export const resetRouter=()=>{
  router.matcher=new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
  })
}


export const setRoutes=()=>{
  const storeMenus = localStorage.getItem("menus");
  if (storeMenus) {


    //获取当前的路由对象名称数组
    const currentRouteNames = router.getRoutes().map(v=>v.name)
    if (!currentRouteNames.includes("Manage")){
      //拼装动态路由
      const manageRoute = {path: '/', name:'Manage',component: () => import("../views/Manage.vue"), redirect: "/home", children: [
          {path: 'person',name:"个人信息", component: () => import("../views/Person.vue")},
          {path: 'password',name:"密码修改", component: () => import("../views/Password.vue")},
        ]}
      const menus = JSON.parse(storeMenus)
      menus.forEach(item => {
        if (item.path) {
          const pageName = item.path.replace("/", "").charAt(0).toUpperCase() + item.path.replace("/", "").slice(1)
          let itemMenu = {
            path: item.path.replace("/", ""),
            name: item.name,
            component: () => import("../views/" + pageName + ".vue")
          }
          manageRoute.children.push(itemMenu)
        } else if (item.children.length) {
          item.children.forEach(childItem => {
            if (childItem.path) {
              const pageName = childItem.path.replace("/", "").charAt(0).toUpperCase() + childItem.path.replace("/", "").slice(1)
              let childMenu = {
                path: childItem.path.replace("/", ""),
                name: childItem.name,
                component: () => import("../views/" + pageName + ".vue")
              }
              manageRoute.children.push(childMenu)
            }
          })
        }
      })
      //动态添加到现在的路由对象中去
      router.addRoute(manageRoute)
    }
  }
}

//重置我就再set一次路由
setRoutes()

/*
router.addRoute(
    {
      path: '/',
      name:"Manage",
      component: ()=>import("../views/Manage.vue"),
      redirect:"/home",

      children:[
        {path: 'home',name:"首页", component: () => import("../views/Home.vue")},
        {path: 'user',name:"用户管理", component: () => import("../views/User.vue") },
        {path: 'role',name:"角色管理", component: () => import("../views/Role.vue") },
        {path: 'menu',name:"菜单管理", component: () => import("../views/Menu.vue") },
        {path: 'person',name:"个人中心", component: () => import("../views/Person.vue")},
        {path: 'file',name:"文件管理", component: () => import("../views/File.vue")}
      ]
    }
)
*/

export default router
