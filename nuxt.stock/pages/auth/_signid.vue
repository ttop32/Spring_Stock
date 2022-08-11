<template>
  <v-row justify="center" align="center">
    <v-col cols="12" sm="10" md="8" xl="6" @keyup.enter="onEnter">
      <v-tabs
        v-model="tab"
        show-arrows
        background-color="green "
        icons-and-text
        dark
        grow
      >
        <v-tabs-slider></v-tabs-slider>
        <v-tab v-for="i in tabs" :key="i.id">
          <v-icon large>{{ i.icon }}</v-icon>
          <div class="caption py-1">{{ i.name }}</div>
        </v-tab>
        <v-tab-item>
          <v-card class="pa-8">
            <v-card-text>
              <v-form ref="loginForm" v-model="validSignIn" lazy-validation>
                <v-row>
                  <TextBox v-model="loginEmail" boxType="email" />
                  <TextBox v-model="loginPassword" boxType="pw" />

                  <v-col class="d-flex" cols="12" sm="6" xsm="12">
                    <NuxtLink
                      to="/profile/passwordresetrequest"
                      @click.native="scroll"
                    >
                      Forgot password?
                    </NuxtLink>
                  </v-col>
                  <v-spacer></v-spacer>
                  <v-col class="d-flex" cols="12" sm="3" xsm="12" align-end>
                    <v-btn x-large block color="success" @click="handleSignIn">
                      Sign In
                    </v-btn>
                  </v-col>
                </v-row>
              </v-form>
            </v-card-text>
          </v-card>
        </v-tab-item>
        <v-tab-item>
          <v-card class="pa-8">
            <v-card-text>
              <v-form ref="registerForm" v-model="validSignUp" lazy-validation>
                <v-row>
                  <TextBox v-model="nickName" boxType="nickname" />
                  <TextBox v-model="email" boxType="email" />
                  <TextBox v-model="password" boxType="pw" />
                  <TextBox v-model="verify" boxType="pwVerify" />

                  <!-- checkbox -->
                  <v-checkbox
                    v-model="checkbox"
                    :rules="[(v) => !!v || 'You must agree to continue!']"
                    required
                  >
                    <template #label>
                      <div>
                        Agree to the
                        <v-tooltip bottom>
                          <template #activator="{ on }">
                            <a
                              target="_blank"
                              href="/policy/terms"
                              @click.stop
                              v-on="on"
                            >
                              terms of service
                            </a>
                          </template>
                          Opens in new window
                        </v-tooltip>
                        and the
                        <v-tooltip bottom>
                          <template #activator="{ on }">
                            <a
                              target="_blank"
                              href="/policy/privacy"
                              @click.stop
                              v-on="on"
                            >
                              privacy policy
                            </a>
                          </template>
                          Opens in new window
                        </v-tooltip>
                      </div>
                    </template>
                  </v-checkbox>

                  <v-spacer></v-spacer>

                  <v-col class="d-flex ml-auto" cols="12" sm="3" xsm="12">
                    <v-btn
                      x-large
                      block:disabled="!validSignUp"
                      color="success"
                      @click="handleSignUp"
                      >Sign Up</v-btn
                    >
                  </v-col>
                </v-row>
              </v-form>
            </v-card-text>
          </v-card>
        </v-tab-item>
      </v-tabs>
    </v-col>
  </v-row>
</template>
<script>
import { useAuthStore } from '~/store/authentication'
import { useRulesStore } from '~/store/rules'
// https://www.codeply.com/p/YTg6nsGf3i

export default {
  name: 'SignInPage',

  setup() {
    const store = useAuthStore()
    const rulesStore = useRulesStore()
    return { store, rulesStore }
  },

  data() {
    return {
      tab: 0,
      tabs: [
        { id: 0, name: 'Sign In', icon: 'mdi-account' },
        { id: 1, name: 'Sign Up', icon: 'mdi-account-outline' },
      ],

      validSignIn: true,
      validSignUp: true,
      showPw: false,

      checkbox: '',
      nickName: '',
      email: '',
      password: '',
      verify: '',
      loginPassword: '',
      loginEmail: '',
    }
  },

  mounted() {
    this.handleUrlParam()
  },

  methods: {
    // showAlert2() {
    //   // this.$toast("I'm a toast!");
    //   this.$swal('Hello Vue world!!!');
    // },
    handleUrlParam() {
      if (this.$route.params.signid === 'signup') {
        this.tab = 1
      } else {
        this.tab = 0
      }
    },
    resetData() {
      this.validSignIn = true
      this.validSignUp = true
      this.showPw = false
    },

    async handleSignIn() {
      if (this.$refs.loginForm.validate()) {
        try {
          await this.store.signIn(this.loginEmail, this.loginPassword)
          this.showSuccessPopUp('Succesfully sign in', this.openMainPage)
        } catch (error) {
          this.handleAuthError(error)
        }
      }
    },
    async handleSignUp() {
      if (this.$refs.registerForm.validate()) {
        try {
          await this.store.signUp(this.nickName, this.email, this.password)
          this.store.requestVerifyEmail()
          this.showSuccessPopUp(
            'Email verification is sended to you. Please verify it to continue',
            this.refreshPage
          )
        } catch (error) {
          this.handleAuthError(error)
        }
      }
    },

    handleAuthError(error) {
      console.log(error.response.data)
      console.log(error.response.status)
      console.log(error.response.headers)
    },
    refreshPage() {
      console.log('refreshPage')
      // this.$router.push('/')
      window.location.reload(true)
    },
    openMainPage() {
      this.$router.push('/')
      // window.location.reload(true)
    },
    openSignInPage() {
      this.$router.push('/auth/signin')
    },
    onEnter() {
      if (this.tab === 1) {
        this.handleSignIn()
      } else {
        this.handleSignUp()
      }
    },

    showSuccessPopUp(message, callback) {
      this.$swal('Good job!', message, 'success').then(callback)
    },
  },
}
</script>
