package me.skean.framework.example.net.bean

data class MovieInfo(
    var `data`: List<Data?>? = null,
    var createdAt: Long? = null,
    var updatedAt: Long? = null,
    var id: String? = null,
    var originalName: String? = null,
    var imdbVotes: Int? = null,
    var imdbRating: String? = null,
    var rottenRating: String? = null,
    var rottenVotes: Int? = null,
    var year: String? = null,
    var imdbId: String? = null,
    var alias: String? = null,
    var doubanId: String? = null,
    var type: String? = null,
    var doubanRating: String? = null,
    var doubanVotes: Int? = null,
    var duration: Int? = null,
    var dateReleased: String? = null
) {
    data class Data(
        var createdAt: Long? = null,
        var updatedAt: Long? = null,
        var id: String? = null,
        var poster: String? = null,
        var name: String? = null,
        var genre: String? = null,
        var description: String? = null,
        var language: String? = null,
        var country: String? = null,
        var lang: String? = null,
        var movie: String? = null
    )
}