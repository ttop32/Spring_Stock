<template>
  <!-- https://www.bezkoder.com/vuetify-pagination-server-side/ -->

  <div class="flex-column">
    <v-data-table
      class="row-pointer"
      :headers="headers"
      :items="stockPageData.contents"
      disable-pagination
      :hide-default-footer="true"
      @click:row="handleTableRowClick"
    >
    </v-data-table>

    <!-- page button -->
    <PageButton
      v-model="currentPage"
      :length="stockPageData.totalPages"
      @submit="handlePageChange"
      :key="pageKey"
    />

    <!--search  -->
    <v-row id="search-bar" class="mx-1">
      <v-col cols="12" sm="3">
        <v-select
          v-model="searchType"
          :items="searchTypeList"
          label="Search Type"
        ></v-select>
      </v-col>
      <v-col cols="12" sm="7">
        <v-text-field
          v-model="searchText"
          label="Search Tickers"
          @keyup.enter="handleSearch"
        ></v-text-field>
      </v-col>
      <v-col cols="12" sm="2">
        <v-btn small @click="handleSearch"> Search </v-btn>
      </v-col>
    </v-row>
  </div>
</template>
<script>
export default {
  name: 'StockListBoard',
  props: {
    caer: {
      type: String,
      default: '',
    },
  },

  data() {
    return {
      stockPageData: [],
      searchTypeList: ['All', 'TickerID', 'TickerName'],
      searchType: this.$route.query.type || 'All',
      searchText: this.$route.query.search || '',
      headers: [
        {
          text: 'Ticker ID',
          sortable: false,
          value: 'tickerId',
        },
        { text: 'Name', value: 'stockName', sortable: false },
      ],
      currentPage: parseInt(this.$route.query.page) || 1,
      pageSize: 20,
      pageKey:0
    }
  },
  watch: {
    '$route.query.page': {
      immediate: true,
      handler(newVal) {
        if (newVal == null) {
          this.currentPage = 1
          this.pageKey+=1
          this.loadPageData()
        }
      },
    },
  },
  async created() {
    await this.loadPageData()
  },

  methods: {
    async handlePageChange() {
      this.queryPage();
      await this.loadPageData()
    },
    async handleSearch() {
      this.currentPage = 1
      this.querySearch();
      await this.getSearchPageData()
    },
    async loadPageData() {
      if (this.searchText === '') {
        await this.getBasePageData()
      } else {
        await this.getSearchPageData()
      }
    },

    async getBasePageData() {
      try {
        this.stockPageData = await this.$axios.$get('/api/stock', {
          params: { page: this.currentPage - 1, size: this.pageSize },
        })
      } catch (error) {
        console.log(error)
      }
    },
    async getSearchPageData() {
      try {
        this.stockPageData = await this.$axios.$get('/api/stock', {
          params: {
            searchParam: this.searchText,
            searchType: this.searchType,
            page: this.currentPage - 1,
            size: this.pageSize,
          },
        })
      } catch (error) {
        console.log(error)
      }
    },


    
    handleTableRowClick(item) {
      this.$router.push({
        path: '/stock/' + item.tickerId,
        query: this.$route.query,
      })
    },

    queryPage() {
      const query = { ...this.$route.query, page: this.currentPage }
      this.$router.replace({ query })
    },
    querySearch() {
      const query = {
        ...this.$route.query,
        search: this.searchText,
        type: this.searchType,
      }
      this.$router.replace({ query })
    },
  },
}
</script>

<style lang="css" scoped>
.row-pointer >>> tbody tr :hover {
  cursor: pointer;
}

#search-bar {
  display: flex;
  align-items: center;
}
</style>
