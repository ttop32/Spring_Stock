<template>
  <v-col cols="12" class="my-8">
    <v-card>
      <v-card-title class="headline">
        {{ cardTitle }}
      </v-card-title>
      <v-card-text class="pa-8">
        <slot />

        <v-card-actions v-if="useSubmitButton || useDeleteButton">
          <v-spacer />
          <v-btn :color="buttonColor" @click="$emit('submit-card')">
            {{ buttonText }}
          </v-btn>
        </v-card-actions>
      </v-card-text>
    </v-card>
  </v-col>
</template>

<script>
export default {
  name: 'ProfileBox',
  props: {
    cardTitle: {
      type: String,
      default: 'no-title',
    },
    useSubmitButton: {
      type: Boolean,
      default: false,
    },
    useDeleteButton: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['submit-card'],
  data() {
    return {
      buttonText: 'Submit',
      buttonColor: 'Primary',
    }
  },
  beforeMount() {
    if (this.useDeleteButton) {
      this.buttonText = 'Delete'
      this.buttonColor = 'red'
    }
  },
  methods:{
    
  }
}
</script>
