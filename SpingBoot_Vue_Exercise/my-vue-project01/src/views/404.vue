
<template>
  <div class="bgImg">
    <h1>404</h1>

  </div>

</template>
<script>
export default {
  name: "Page404"
}
</script>

<style scoped>
.bgImg {
  width: 100%;
  height: 100vh;
  background: url('~@/assets/404.jpg') no-repeat;
  background-size: 100% 100%;
  /* 文字水平+垂直居中 */
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}
.bgImg h1 {
  color: #fff;
  font-size: 48px;
}
</style>