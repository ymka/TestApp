package net.ginapps.testapp.data

sealed class Method(val name: String) {
    data object PersonalSign : Method("personal_sign")
}
