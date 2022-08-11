<template>
  <div>
    <v-row justify="center" align="center">
      <v-col cols="12" sm="8" md="6">
        <ProfileBox cardTitle="Current Profile">
          <p>Status : {{ store.status }}</p>
          <p>Nickname : {{ store.nickname }}</p>
          <p>Email : {{ store.email }}</p>
          <p>Roles : {{ store.roles }}</p>
        </ProfileBox>
        <hr class="ma-3" />

        <ProfileBox
          cardTitle="Change Profile"
          :useSubmitButton="true"
          @submit-card="handleNicknameSubmit"
        >
          <v-form @submit.prevent ref="nicknameChangeForm" lazy-validation>
            <v-row>
              <TextBox
                v-model="nickname"
                textLabel="New Nickname"
                boxType="nickname"
              />
            </v-row>
          </v-form>
        </ProfileBox>
        <hr class="ma-3" />

        <ProfileBox
          cardTitle="Change Password"
          :useSubmitButton="true"
          @submit-card="handlePasswordSubmit"
        >
          <v-form @submit.prevent ref="pwChangeForm" lazy-validation>
            <v-row>
              <TextBox
                v-model="prevPassword"
                :noPwStore="true"
                textLabel="Previouse Password"
                boxType="pw"
              />
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
        <hr class="ma-3" />

        <ProfileBox
          cardTitle="Delete your account"
          :useDeleteButton="true"
          @submit-card="handleDeleteSubmit"
        >
          <p>
            Once user decided to delete own account, there is no going back.
            <br />
            Please be sure

            <v-form @submit.prevent ref="deleteForm" lazy-validation>
              <v-row>
                <TextBox
                  v-model="passwordDelete"
                  :noPwStore="true"
                  textLabel="Password"
                  boxType="pw"
                />
              </v-row>
            </v-form>
          </p>
        </ProfileBox>
      </v-col>
    </v-row>

    <v-divider></v-divider>
  </div>
</template>
<script>
import { useAuthStore } from '~/store/authentication'

export default {
  name: 'UserInfoPage',

  setup() {
    const store = useAuthStore()
    return { store }
  },
  asyncData() {},
  data() {
    return {
      nickname: this.store.nickname,

      prevPassword: '',
      newPassword: '',
      newPasswordVerify: '',
      showPw: false,

      passwordDelete: '',
    }
  },

  methods: {
    async handleNicknameSubmit() {
      if (this.$refs.nicknameChangeForm.validate()) {
        try {
          await this.store.changeUserInfo(this.nickname)
          this.showSuccessPopup('Succesfully changed', '')
        } catch (error) {
          console.log(error)
        }
      }
    },
    async handlePasswordSubmit() {
      if (this.$refs.pwChangeForm.validate()) {
        try {
          await this.store.changePasswordWithPrevPw(
            this.prevPassword,
            this.newPassword
          )
          this.showSuccessPopup('Succesfully changed', '')
        } catch (error) {
          console.log(error)
        }
      }
    },
    async handleDeleteSubmit() {
      try {
        await this.store.deleteUser(this.passwordDelete)
        this.showSuccessPopup('Succesfully deleted', this.openMainPage)
      } catch (error) {
        console.log(error)
      }
    },
    showSuccessPopup(message, callback) {
      this.$swal('Good job!', message, 'success').then(callback)
    },
    openMainPage() {
      this.$router.push('/')
    },
  },
}
</script>
