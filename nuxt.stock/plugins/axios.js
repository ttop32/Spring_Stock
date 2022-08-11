// https://zaengle.com/blog/error-handling-in-nuxt-apps

import Vue from 'vue'

import { useAuthStore } from '~/store/authentication'

export default ({ $axios, redirect, app }, inject) => {
  $axios.onError((error) => {
    let swalErrorMessage = ''

    if (
      error.response &&
      (error.response.status === 502 || error.response.status === 500)
    ) {
      console.log('No backend server')
      swalErrorMessage = 'No backend server'
    } else if (error.response && error.response.data.code === 'INVALID010') {
      swalErrorMessage = 'Given email is already registered'
    } else if (error.response && error.response.data.code === 'AUTH003') {
      swalErrorMessage = 'Wrong Password or ID'
    } else if (error.response && error.response.data.code === 'AUTH004') {
      swalErrorMessage =
        'Account email is not verified. Verification email is sended. Please check email'
      const store = useAuthStore()
      store.requestVerifyEmail()
    } else if (error.response && error.response.data.code === 'INVALID009') {
      // filed requirement is not enough
      swalErrorMessage = error.response.data.errors[0].field
      swalErrorMessage += ' : '
      swalErrorMessage += error.response.data.errors[0].reason
    } else if (
      error.response &&
      (error.response.data.code === 'NOT006' ||
        error.response.data.code === 'INVALID004')
    ) {
      swalErrorMessage = 'Invalid email token'
    } else if (error.response) {
      // Request made and server responded
      console.log(error.response.data)
      console.log(error.response.status)
      console.log(error.response.headers)
      throw error
    } else if (error.request) {
      console.log('The request was made but no response was received')
      swalErrorMessage = 'Network Error: Please refresh and try again.'
    } else {
      console.log(
        'Something happened in setting up the request that triggered an Error'
      )
      swalErrorMessage = 'Network Error: Please refresh and try again.'
    }

    if (swalErrorMessage !== '') {
      Vue.swal('Oops...', swalErrorMessage, 'error')
      // Vue.$toast("My toast content");
    }
  })
}
