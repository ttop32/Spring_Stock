<template>
  <v-row justify="center" align="center">
    <v-col cols="12" sm="10" md="8">
      <v-card>
        <v-card-title class="headline">
          {{ title }}
        </v-card-title>
        <v-card-text>
          <slot />

        </v-card-text>
      </v-card>
    </v-col>
  </v-row>
</template>

<script>
export default {
  name: 'MainBoard',
  props: {
    title: {
      type: String,
      default: 'title',
    },
  },
  data() {
    return {
      content: '',
    }
  },

}
</script>
