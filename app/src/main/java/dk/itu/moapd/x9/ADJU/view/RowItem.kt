package dk.itu.moapd.x9.ADJU.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import dk.itu.moapd.x9.ADJU.R
import dk.itu.moapd.x9.ADJU.model.TrafficReport

@Composable
fun RowItem(
    model: TrafficReport,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.padding(dimensionResource(R.dimen.margin_standard))
    ) {
        Column {
            TextElem(model)
        }
    }
}

@Composable
private fun TextElem(model: TrafficReport) {
    Column(
        modifier = Modifier.padding(dimensionResource(R.dimen.margin_medium)).fillMaxWidth()
    ) {
        Text(
            text = model.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = model.description,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.margin_medium))
        )
        Text(
            text = model.state,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.margin_medium))
        )
    }
}
