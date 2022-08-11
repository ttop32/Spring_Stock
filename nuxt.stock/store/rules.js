import { defineStore } from "pinia";


export const useRulesStore = defineStore("rules", {
  state: () => ({     
    pwInput:"",
    pwVerify:"" 
  }),
  actions: {
    resetPw() {
      this.pwInput="";
      this.pwVerify="";
    },
  },
  getters: {
  }
});