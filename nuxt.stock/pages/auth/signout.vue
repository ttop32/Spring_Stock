<template>
      <MainBoard :title="title" />

</template>
<script>
import { useAuthStore } from '~/store/authentication'


export default {
  name: 'SignOutPage',

  setup() {
    const store = useAuthStore()
    return { store }
  },

  data() {
    return {
      title: "Try sign out",
    }
  },

  async beforeMount(){
    console.log("signout")
    await this.store.signOut()
    this.$router.push('/')
  }
}
</script>
