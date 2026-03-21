package dk.itu.moapd.x9.ADJU.viewmodel

data class MainUiState (
    val userId: String? = null,
    val reports: List<ReportUi> = emptyList(),
)
