package zielu.gittoolbox.fetch

import com.intellij.openapi.project.Project
import zielu.gittoolbox.extension.autofetch.AUTO_FETCH_ALLOWED_TOPIC
import zielu.gittoolbox.util.LocalGateway

internal class AutoFetchAllowedLocalGateway(private val project: Project) : LocalGateway(project) {

  fun fireStateChanged() {
    runInBackground { project.messageBus.syncPublisher(AUTO_FETCH_ALLOWED_TOPIC).stateChanged() }
  }
}
