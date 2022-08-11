module.exports = {
  root: true,
  env: {
    browser: true,
    node: true,
  },
  parserOptions: {
    parser: '@babel/eslint-parser',
    requireConfigFile: false,
  },
  extends: ['@nuxtjs', 'plugin:nuxt/recommended', 'prettier'],
  plugins: [],
  // add your custom rules here
  rules: { 
    
    "vue/attribute-hyphenation": 0,
    "no-console": "off" ,
    'vue/multi-word-component-names': ['error', {
      'ignores': ['default']
    }],
  },
}
