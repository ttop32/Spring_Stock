<template>
  <div v-if="isGraphDataExist">
    <div class="flex-column">
      <p class="text-h5 px-2">
        {{ stockDetailData.stockName }} : {{ stockDetailData.tickerId }}
      </p>

      <div id="chart" class="black--text"></div>
    </div>
  </div>

  <div v-else>
    <p class="text-center body-1">
      <v-icon left> mdi-archive-remove </v-icon>
      No Stock Data
      <span v-if="stockDetailData.stockName">
        for  {{ stockDetailData.stockName }}
      </span>
    </p>
  </div>
</template>
<script>
import { bb, line } from 'billboard.js'
import 'billboard.js/dist/theme/insight.css'

export default {
  name: 'StockDetailBoard',
  props: {
    tickerId: {
      type: String,
      default: 'tickerlg',
    },
  },

  data() {
    return {
      stockGraphData: {},
      stockDetailData: {},
    }
  },
  computed: {
    isGraphDataExist() {
      return this.stockGraphData.dateSet && this.stockGraphData.dateSet.length
    },
  },
  watch: {
    async tickerId(newVal, oldvar) {
      // watch it
      await this.loadStockDetailData()
      await this.loadStockGraphData()
      this.initGraph()
    },
  },

  async created() {
    await this.loadStockDetailData()
    await this.loadStockGraphData()

    this.initGraph()
  },
  

  methods: {
    async loadStockDetailData() {
      try {
        this.stockDetailData = await this.$axios.$get('/api/stock/detail/', {
          params: { tickerId: this.tickerId },
        })
      } catch (error) {
        this.noTickerDetail = true
        console.log(error)
      }
    },
    async loadStockGraphData() {
      try {
        this.stockGraphData = await this.$axios.$get(
          `/api/stockgraph/` + this.tickerId+"/year"
        )
      } catch (error) {
        console.log(error)
      }
    },
    initGraph() {
      const columData = []

      if (this.isGraphDataExist) {
        // x-axis date
        columData.push(['x', ...this.stockGraphData.dateSet])

        // graph data
        for (const key in this.stockGraphData.stockGraphMap) {
          columData.push([key, ...this.stockGraphData.stockGraphMap[key]])
        }

        bb.generate({
          bindto: '#chart',
          data: {
            x: 'x',
            type: line(),
            columns: columData,
          },
          axis: {
            x: {
              type: 'timeseries',
              tick: {
                format: '%Y-%m-%d',
              },
            },
          },
        })
      }
    },
  },
}
</script>