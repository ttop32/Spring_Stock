<template>
  <div>
    <div v-if="showCommentBox || isAlwaysBoxShow">
      <v-textarea
        v-model="content"
        :label="label"
        outlined
        :disabled="!loginStatus"
      ></v-textarea>
      <div class="d-flex justify-end">
        <v-btn text small tile color="green" @click="$emit('submit')">
          <v-icon left> mdi-pencil </v-icon>
          Submit
        </v-btn>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CommentWriteBox',
  props: {
    value: {
      type: String,
      default: '',
    },
    boxType: {
      type: String,
      default: 'comment',
    },
    showCommentBox: {
      type: Boolean,
      default: false,
    },
    loginStatus: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['submit'],
  data() {
    return {
      content: this.value,
      label: 'Write Comment',
      isAlwaysBoxShow: true,
    }
  },
  watch: {
    content(e) {
      // handle v-model change
      this.$emit('input', this.content)
    },
    value(newVal) {
      this.content = newVal
    },
  },
  beforeMount() {
    if (this.boxType === 'reply') {
      this.label = 'Write Reply'
      this.isAlwaysBoxShow = false
    } else if (this.boxType === 'edit') {
      this.label = 'Edit Comment'
      this.isAlwaysBoxShow = false
    } else {
      this.label = 'Write Comment'
      this.isAlwaysBoxShow = true
    }

    if (this.loginStatus === false) {
      this.label = 'Sign In is Required'
    }
  },
}
</script>
