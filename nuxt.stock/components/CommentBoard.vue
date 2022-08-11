<!-- Please remove this file from your project -->
<template>
  <div>
    <!-- comment display -->
    <div v-if="commentData.content && commentData.content.length">
      <div v-for="(comment, index) in commentData.content" :key="comment.id">
        <v-row :class="{ 'ml-5': comment.isReply }">
          <v-col cols="2">
            <div class="flex-column">
              <v-img src="/user.png" width="64" height="64" class="mx-auto" />

              <p v-if="comment.member" class="text-center">
                {{ comment.member.nickname }}
              </p>
              <p v-else class="text-center">Deleted</p>
            </div>
          </v-col>

          <v-col cols="10">
            <div class="d-flex">
              <p class="mr-auto">
                <timeago :datetime="comment.modifiedDate"></timeago>
              </p>
              <v-btn
                v-if="!comment.isReply"
                text
                small
                tile
                color="primary"
                @click="handleReplyShowButton(index)"
              >
                <v-icon left> mdi-reply </v-icon>
                Reply
              </v-btn>
              <template v-if="comment.member && comment.member.id === store.id">
                <v-btn
                  text
                  small
                  tile
                  color="green"
                  @click="handleEditShowButton(index, comment.content)"
                >
                  <v-icon left> mdi-pencil-plus </v-icon>
                  Edit
                </v-btn>

                <v-btn
                  text
                  small
                  tile
                  color="red"
                  @click="deleteComment(comment.id)"
                >
                  <v-icon left> mdi-delete </v-icon>
                  Delete
                </v-btn>
              </template>
            </div>
            <p style="white-space: pre-wrap">{{ comment.content }}</p>
            <CommentWriteBox
              v-model="editText"
              boxType="edit"
              :showCommentBox="index === editExpandIndex"
              :loginStatus="store.status === 'authenticated'"
              @submit="handleEditSubmit(comment.id)"
            />
          </v-col>
        </v-row>

        <CommentWriteBox
          v-model="replyText"
          boxType="reply"
          :showCommentBox="index === replyExpandIndex"
          :loginStatus="store.status === 'authenticated'"
          @submit="handleReplySubmit(comment.id)"
        />
      </div>
    </div>

    <!-- no comment -->
    <div v-else>
      <p class="text-center body-1">
        <v-icon left> mdi-comment-remove </v-icon>
        No Comment
      </p>
    </div>

    <PageButton
      v-model="currentPage"
      :length="commentData.totalPages"
      @submit="handlePageChange"
    />

    <CommentWriteBox
      v-model="commentText"
      :loginStatus="store.status === 'authenticated'"
      @submit="handleCommentSubmit()"
    />
  </div>
</template>

<script>
import { useAuthStore } from '~/store/authentication'

export default {
  name: 'CommentBoard',
  props: {
    tickerId: {
      type: String,
      default: 'tickerlg',
    },
  },
  setup() {
    const store = useAuthStore()
    return { store }
  },
  data() {
    return {
      commentData: [],
      timeAgo: '',

      replyExpandIndex: -1,
      editExpandIndex: -1,

      commentText: '',
      replyText: '',
      editText: '',

      currentPage: 1,
      pageSize: 30,
    }
  },

  watch: {
    commentData(val) {
      this.replyExpandIndex = -1
      this.editExpandIndex = -1
    },
    tickerId(newVal, oldvar) {
      // watch it
      this.loadCommentData()
    },
  },
  async beforeMount() {
    await this.loadCommentData()
  },
  methods: {
    async loadCommentData() {
      try {
        this.commentData = await this.$axios.$get('/api/comment', {
          params: {
            tickerId: this.tickerId,
            page: this.currentPage - 1,
            size: 30,
          },
        })
      } catch (error) {
        console.log(error)
      }
    },
    async addComment(tickerId, content, replyParentId) {
      try {
        await this.$axios.$post('/api/comment', {
          tickerId,
          content,
          replyParentId,
        })
        this.showSuccessPopUp('Comment is added')
      } catch (error) {
        console.log(error)
      }
    },
    async editComment(commentId, content) {
      try {
        await this.$axios.$put('/api/comment', {
          commentId,
          content,
        })
        this.showSuccessPopUp('Comment is edited')
      } catch (error) {
        console.log(error)
      }
    },
    async deleteComment(commentId) {
      try {
        await this.$axios.$delete('/api/comment/' + commentId)
        this.showSuccessPopUp('Comment is removed')
      } catch (error) {
        console.log(error)
      }
    },
    showSuccessPopUp(message) {
      this.$swal('Good job!', message, 'success').then(this.loadCommentData)
    },

    handleReplyShowButton(value) {
      this.replyExpandIndex = value
      this.editExpandIndex = -1
      this.replyText = ''
    },
    handleEditShowButton(value, comment) {
      this.replyExpandIndex = -1
      this.editExpandIndex = value
      this.editText = comment
    },
    async handlePageChange(value) {
      await this.loadCommentData()
    },
    handleCommentSubmit() {
      this.addComment(this.tickerId, this.commentText)
    },
    handleReplySubmit(commentId) {
      this.addComment(this.tickerId, this.replyText, commentId)
    },
    handleEditSubmit(commentId) {
      this.editComment(commentId, this.commentText)
    },
  },
}
</script>
