package app.callcheck.mobile.data.contacts

data class ContactInfo(
    val name: String?,
    val phoneNumber: String?,
    val photoUri: String?,
    val isFavorite: Boolean = false,
)
