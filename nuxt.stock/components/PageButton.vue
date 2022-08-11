<template>
  <div>
    <v-col cols="12" sm="12">
      <v-pagination
        v-model="page"
        :length="length"
        total-visible="7"
        next-icon="mdi-menu-right"
        prev-icon="mdi-menu-left"
        @input="handleSubmit"
      ></v-pagination>
    </v-col>
  </div>
</template>

<script>
export default {
  name: 'PageButton',
  props: {
    value: {
      type: Number,
      default: 1,
    },
    length: {
      type: Number,
      default: 1,
    },
  },
  emits: ['submit'],
  data() {
    return {
      page: this.value,
    }
  },
  methods: {
    handleSubmit() {
      this.$emit('input', this.page)
      this.$emit('submit', this.page)
    },
  },
}
</script>
