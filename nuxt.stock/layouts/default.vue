<template>
  <v-app dark>
    <h3>Spring Stock</h3>

    <v-app-bar :clipped-left="clipped" fixed app >
      <NuxtLink to="/" class="d-flex" @click.native="scroll">
        <v-avatar :tile="true">
          <img src="/icon.png" alt="logo" to="/" />
        </v-avatar>
        <v-toolbar-title class="align-self-center" v-text="title" />
      </NuxtLink>

      <v-spacer />
      <template v-for="button in buttonList">
        <v-btn
          v-if="filterButton(button.showStatus)"
          :key="button.name"
          :to="button.url"
          text
          rounded
          class="my-2"
        >
          <v-icon left>
            {{ button.icon }}
          </v-icon>
          {{ button.name }}
        </v-btn>
      </template>
    </v-app-bar>
    <v-main>
      <v-container>
        <Nuxt />
      </v-container>
    </v-main>

    <v-footer
      :absolute="!fixed"
      app
      class="py-4 text-center"
      :color="color"
    >
      <v-row justify="center" no-gutters>
        <v-btn
          v-for="link in links"
          :key="link.name"
          :to="link.url"
          text
          rounded
          class="my-2"
        >
          {{ link.name }}
        </v-btn>
        <v-col cols="12">
          Copyright © {{ currentYear }} <strong>{{ title }}</strong> All rights
          reserved
        </v-col>
      </v-row>
    </v-footer>
  </v-app>
</template>

<script>
import { useAuthStore } from '~/store/authentication'

export default {
  name: 'DefaultLayout',

  setup() {
    const authStore = useAuthStore()    
    return { authStore }
  },
  data() {
    return {
      clipped: false,
      fixed: false,
      title: 'Spring Stock',
      color: "green ",

      links: [
        { url: '/', name: 'Home' },
        { url: '/policy/terms', name: 'Terms' },
        { url: '/policy/privacy', name: 'Privacy' },
      ],
      buttonList: [
        {
          url: '/auth/signin',
          showStatus: 'anonymouse',
          name: 'Sign in',
          icon: 'mdi-login',
        },
        {
          url: '/auth/signup',
          showStatus: 'anonymouse',
          name: 'Sign up',
          icon: 'mdi-account-check',
        },
        {
          url: '/profile/userinfo',
          showStatus: 'authenticated',
          name: 'Profile',
          icon: 'mdi-shield-account',
        },
        {
          url: '/auth/signout',
          showStatus: 'authenticated',
          name: 'Sign out',
          icon: 'mdi-logout',
        },
      ],

      currentYear: new Date().getFullYear(),
    }
  },
  async BeforeMount() {
    await this.authStore.updateMemberInfo()
  },
  methods: {
    filterButton(status) {
      if (status === 'always') {
        return true
      } else if (status === this.authStore.status) {
        return true
      } else {
        return false
      }
    },
    scroll(){
      window.scrollTo({
        top: 0,
        left: 0,
        behavior: 'smooth'
      });
    }
  },
}
</script>
<style scoped>
.nuxt-link-active {
  text-decoration: none;
  color: inherit;
}
</style>
