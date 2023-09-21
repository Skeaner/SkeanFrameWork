package me.skean.framework.example.net.bean

data class ArticleResponse(
    val `data`: PageWrapper,
    val errorCode: Int,
    val errorMsg: String
){
    data class PageWrapper(
        val curPage: Int,
        val datas: List<ArticleData>,
        val offset: Int,
        val over: Boolean,
        val pageCount: Int,
        val size: Int,
        val total: Int
    )

}


