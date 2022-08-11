<template>
  <div>
    <MainBoard title="Stock Detail">
      <StockDetailBoard :tickerId="tickerId" />
    </MainBoard>
    <MainBoard title="Comment">
      <CommentBoard :tickerId="tickerId" />
    </MainBoard>
    <MainBoard title="Stock List">
      <StockListBoard />
    </MainBoard>
  </div>
</template>

<script>
export default {
  name: 'StockDetailPage',
  data() {
    return {
      tickerId: '',
    }
  },
  beforeMount() {
    this.tickerId = this.$route.params.tickerid
  },
  
  watch: {
    "$route.params.tickerid"(value) {
        this.tickerId = value
    }
  }
}
</script>
