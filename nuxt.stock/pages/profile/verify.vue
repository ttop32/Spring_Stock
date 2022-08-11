<template>
  <MainBoard :title="title" />
</template>
<script>
import { useAuthStore } from '~/store/authentication'

export default {
  name: 'EmailVerifyPage',

  setup() {
    const store = useAuthStore()
    return { store }
  },

  data() {
    return {
      title: 'Try email verify',
      token: this.$route.query.token || '',
    }
  },

  async beforeMount() {
    try {
      await this.store.verifyEmail(this.token)
      this.showSuccessPopUp('Email is succenfully verified')
    } catch (error) {
      console.log(error)
    }
    this.redirectToSignInPage()
  },
  methods: {
    showSuccessPopUp(message, callback) {
      this.$swal('Good job!', message, 'success').then(callback)
    },
    redirectToSignInPage() {
      this.$router.push('/auth/signin')
    },
  },
}
</script>
