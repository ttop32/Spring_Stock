<template>
  <v-row justify="center" align="center">
    <v-col cols="12" sm="8" md="6">
      <ProfileBox
        :cardTitle="title"
        :useSubmitButton="true"
        @submit-card="handleChangePassowd"
      >
        <v-form ref="pwChangeForm" lazy-validation>
          <v-row>
            <TextBox
              v-model="newPassword"
              textLabel="New Password"
              boxType="pw"
            />
            <TextBox
              v-model="newPasswordVerify"
              textLabel="Confirm New Password"
              boxType="pwVerify"
            />
          </v-row>
        </v-form>
      </ProfileBox>
    </v-col>
  </v-row>
</template>
<script>
import { useAuthStore } from '~/store/authentication'

export default {
  name: 'PasswordResetPage',

  setup() {
    const store = useAuthStore()
    return { store }
  },

  data() {
    return {
      title: 'Reset Password',
      token: this.$route.query.token || '',
      newPassword: '',
      newPasswordVerify: '',
    }
  },

  async beforeMount() {},
  methods: {
    async handleChangePassowd() {
      try {
        await this.store.changePasswordWithEmailToken(
          this.newPassword,
          this.token
        )
        this.showSuccessPopUp('Password is succenfully changed')
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
