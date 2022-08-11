<template>
  <MainBoard :title="title">
    <pre>{{ content }}</pre>
  </MainBoard>
</template>

<script>
import privacy from '@/assets/privacy.txt'
import terms from '@/assets/terms.txt'

export default {
  name: 'PolicyPage',

  data() {
    return {
      title: '',
      content: '',
    }
  },
  beforeMount() {
    if (this.$route.params.policyid === 'privacy') {
      this.title = 'Prviacy Policy'
      this.content = privacy
    } else {
      this.title = 'Terms of use'
      this.content = terms
    }
  },
}
</script>

<style scoped>
pre {
  white-space: pre-wrap;
}
</style>
