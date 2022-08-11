<template>
  <v-col cols="12">
    <v-text-field
      v-model="content"
      block
      :append-icon="pwShowIcon"
      :rules="currentRules"
      :type="textType"
      :label="currentLabel"
      @click:append="showPw = !showPw"
    ></v-text-field>
  </v-col>
</template>

<script>
import { useRulesStore } from '~/store/rules'

export default {
  name: 'TextBox',
  props: {
    value: {
      type: String,
      default: '',
    },
    boxType: {
      type: String,
      default: '',
    },
    textLabel: {
      type: String,
      default: '',
    },
    noPwStore: {
      type: Boolean,
      default: false,
    },
  },
  setup() {
    const store = useRulesStore()
    return { store }
  },
  data() {
    return {
      content: this.value,
      currentRules: [],
      currentLabel: '',

      showPw: false,
      nicknameMinLen: 3,
      nicknameMaxLen: 20,
      pwCount: 8,

      rules: {
        required: (value) => !!value || 'Required.',
        min: (v, count) =>
          (v && v.length >= count) || 'Min ' + count + ' characters',
        max: (v, count) =>
          (v && v.length <= count) || 'Max ' + count + ' characters',
        email: (v) => /.+@.+\..+/.test(v) || 'E-mail must be valid',
      },
      emailRules: [(v) => this.rules.required(v), (v) => this.rules.email(v)],
      nicknameRules: [
        (v) => this.rules.required(v),
        (v) => this.rules.min(v, this.nicknameMinLen),
        (v) => this.rules.max(v, this.nicknameMaxLen),
      ],
      pwRules: [
        (v) => this.rules.required(v),
        (v) => this.rules.min(v, this.pwCount),
      ],
      pwVerify: [
        (v) => this.rules.required(v),
        (v) => this.rules.min(v, this.pwCount),
        (v) => this.passwordMatch(),
      ],

      labelList: {
        email: 'E-mail',
        pw: 'Password',
        pwVerify: 'Confirm Password',
        nickname: 'Nickname',
      },
    }
  },
  computed: {
    isPw() {
      return this.boxType === 'pw' || this.boxType === 'pwVerify'
    },
    passwordMatch() {
      return () =>
        this.store.pwInput === this.store.pwVerify || 'Password must match'
    },
    pwShowIcon() {
      if (this.isPw) {
        return this.showPw ? 'mdi-eye' : 'mdi-eye-off'
      }
      return ''
    },
    textType() {
      if (this.isPw) {
        return this.showPw ? 'text' : 'password'
      }
      return 'text'
    },
  },

  watch: {
    content(e) {
      if (this.noPwStore === false) {
        if (this.boxType === 'pw') {
          this.store.pwInput = e
        }
        if (this.boxType === 'pwVerify') {
          this.store.pwVerify = e
        }
      }
      // handle v-model change
      this.$emit('input', this.content)
    },
  },
  beforeMount() {
    this.setRule()
    this.setLabel()
  },
  beforeUnmount() {
    if (this.isPw) {
      this.store.resetPw()
    }
  },

  methods: {
    setLabel() {
      console.log(this.textLabel)
      if (this.textLabel !== '') {
        this.currentLabel = this.textLabel
      } else {
        this.currentLabel = this.labelList[this.boxType]
      }
    },
    setRule() {
      if (this.boxType === 'email') {
        this.currentRules = this.emailRules
      } else if (this.boxType === 'pw') {
        this.currentRules = this.pwRules
      } else if (this.boxType === 'pwVerify') {
        this.currentRules = this.pwVerify
      } else if (this.boxType === 'nickname') {
        this.currentRules = this.nicknameRules
      }
    },
  },
}
</script>
