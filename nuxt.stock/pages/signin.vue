<template>
  <div>

    <button @click="handleOAuth">oauth in</button>
    {{ oauth }}

    <!-- http://localhost:8080/oauth2/authorize/google?redirect_uri=www.naver.com -->



    <a href="/api/oauth2/authorization/google?redirect_uri=www.naver.com">
      Nuxt oauth1
    </a>


    <a href="/api/oauth2/authorize/google?redirect_uri=http://www.naver.com">
      Nuxt oauth2
    </a>

    <a
      href="http://sub.ttop324.ddnsfree.com/oauth2/authorize/google?redirect_uri=http://front.ttop324.ddnsfree.com/signin"
    >
      Nuxt oauth3
    </a>

    {{ currentUrl }}
  </div>
</template>
<script>
// import qs from 'qs';

export default {
  name: 'SignInPage',

  data() {
    return {
      currentUrl: '',
      signInData: [],
      signUpData: [],
      userData: [],
      oauth: [],
    }
  },

  async fetch() {
    // this.courses = await this.$axios.$get('/api/status/alive')
    // this.page = await this.$axios.$get('api/stock/')
  },

  mounted() {
    this.$cookies.set('SESSION', this.$route.query.token, {
      path: '/',
      maxAge: 60 * 60 * 24 * 7,
    })
  },

  beforeMount() {
    this.currentUrl = window.location.href

    const cookieRes = this.$cookies.get('cookie-name8')
    console.log(cookieRes)

    this.$cookies.set(
      'cookie-name9',
      'cookie-value' + Math.floor(Math.random() * 10)
    )

    this.$cookies.set('SESSION', this.$route.query.token, {
      path: '/',
      maxAge: 60 * 60 * 24 * 7,
    })

    console.log(this.$cookies.get('SESSION'))
  },

  methods: {
    
    async handleOAuth() {
      this.oauth = await this.$axios.get('/api/oauth2/authorization/google')
    },
  },
}
</script>
