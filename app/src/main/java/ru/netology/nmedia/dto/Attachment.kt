import ru.netology.nmedia.enumeration.AttachmentType

data class Attachment(
    val url: String,
    //val description: String? = null,
    val type: AttachmentType,
) {
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            Attachment(it.url, it.type)
        }
    }
}