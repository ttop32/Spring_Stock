<template>
  <v-row justify="center" align="center">
    <v-col cols="12" sm="8" md="6">
      <ProfileBox
        :cardTitle="title"
        :useSubmitButton="true"
        @submit-card="handlePasswordRequest"
      >
        <v-form  @submit.prevent ref="emailForm" lazy-validation>
          <v-row>
            <TextBox v-model="email" boxType="email" />
          </v-row>
        </v-form>
      </ProfileBox>
    </v-col>
  </v-row>
</template>
<script>
import { useAuthStore } from '~/store/authentication'

export default {
  name: 'PasswordResetRequestPage',

  setup() {
    const store = useAuthStore()
    return { store }
  },

  data() {
    return {
      title: 'Password reset email request',
      email: '',
    }
  },

  async beforeMount() {},
  methods: {
    async handlePasswordRequest() {
      try {
        await this.store.requestPasswordResetEmail(    this.email    )
        this.showSuccessPopUp('Password reset email is succenfully sended')
      } catch (error) {
        console.log(error)
      }
      this.redirectToSignInPage()
    },
    showSuccessPopUp(message, callback) {
      this.$swal('Good job!', message, 'success').then(callback)
    },
    redirectToSignInPage() {
      this.$router.push('/auth/signin')
    },
  },
}
</script>
