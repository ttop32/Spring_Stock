import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    status: 'anonymouse',
    id: '',
    email: '',
    nickname: '',
    picture: '',
    roles: '',
    recentEmail: '',
  }),
  actions: {
    async checkStatus() {
      this.status = await this.$nuxt.$axios.$get('/api/status/alive')
    },
    async signUp(nickname, email, password) {
      this.recentEmail = email

      const data = {
        email,
        password,
        nickname,
      }
      await this.$nuxt.$axios.$post('/api/member', data)
    },
    async signIn(email, pw) {
      this.recentEmail = email

      const signInData = await this.$nuxt.$axios.$post('/api/member/login', {
        username: email,
        password: pw,
      })
      this.setAccountInfo(signInData.principal)
    },
    async updateMemberInfo() {
      try {
        const userInfo = await this.$nuxt.$axios.$get('/api/member')
        this.setAccountInfo(userInfo)
      } catch (error) {
        console.log('no login')
      }
    },
    async signOut() {
      this.resetAccountInfo()
      await this.$nuxt.$axios.$get('/api/user/logout')
    },

    async deleteUser(pw) {
      // const data= {  password:pw}
      await this.$nuxt.$axios.$delete('/api/member/')
      this.resetAccountInfo()
    },
    async changeUserInfo(name) {
      const data = { nickname: name }
      const userData = await this.$nuxt.$axios.$put(
        '/api/member/userinfo',
        data
      )
      console.log('eeehe')
      console.log(userData)
      console.log(userData.nickname)
      this.nickname = userData.nickname
    },
    async changePasswordWithPrevPw(prevPw, pw) {
      const data = { password: pw, previousPassword: prevPw }
      await this.$nuxt.$axios.$put('/api/member/password/prevpw', data)
    },

    async changePasswordWithEmailToken(pw,token) {
      const data = { password: pw, emailTokenId: token }
      await this.$nuxt.$axios.$put('/api/member/password/emailtoken', data)
    },


    setAccountInfo(userData) {
      this.id = userData.id
      this.email = userData.email
      this.nickname = userData.nickname
      this.picture = userData.picture
      this.status = 'authenticated'
      this.setRoles(userData.authorities)
    },
    resetAccountInfo() {
      this.id = ''
      this.email = ''
      this.nickname = ''
      this.picture = ''
      this.roles = []
      this.status = 'anonymouse'
    },
    setRoles(authorities) {
      const roles = []
      for (const role of authorities) {
        roles.push(role.authority)
      }
      this.roles = roles
    },
    async requestVerifyEmail() {
      const data = { email: this.recentEmail, isFront: 'true' }
      await this.$nuxt.$axios.$post(
        '/api/member/email/send/emailverification',
        data
      )
    },
    async requestPasswordResetEmail(email) {
      const data = { email, isFront: 'true' }

      await this.$nuxt.$axios.$post(
        '/api/member/email/send/passwordreset',
        data
      )
    },

    async verifyEmail(token) {
      await this.$nuxt.$axios.$get('/api/member/email/verify?token='+token)
    },
  },
  // getters: {
  //   // doubleCount(state) {
  //   //   return state.count * 2;
  //   // }
  // },
})
