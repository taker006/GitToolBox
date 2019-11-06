package zielu.gittoolbox.blame.calculator

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.history.VcsRevisionNumber
import git4idea.GitRevisionNumber
import git4idea.commands.GitCommandResult
import git4idea.commands.GitLineHandler
import git4idea.repo.GitRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import zielu.TestType
import zielu.intellij.test.MockVfsUtil

@Tag(TestType.FAST)
@ExtendWith(MockKExtension::class)
internal class BlameCalculatorTest {
  private val vFileMock = MockVfsUtil.createFile("/path/to/file.txt")

  @Test
  internal fun annotateReturnsNullIfNoCurrentRevisionNumber(
    @MockK projectMock: Project,
    @MockK repoMock: GitRepository,
    @MockK gatewayMock: BlameCalculatorLocalGateway
  ) {
    // given
    every { gatewayMock.getCurrentRevisionNumber(vFileMock) } returns VcsRevisionNumber.NULL
    val calculator = BlameCalculator(projectMock, gatewayMock)

    // when
    val dataProvider = calculator.annotate(repoMock, vFileMock)

    // then
    assertThat(dataProvider).isNull()
  }

  @Test
  internal fun annotateReturnsResultIfCurrentRevisionNumberPresentAndCommandSuccess(
    @MockK projectMock: Project,
    @MockK repoMock: GitRepository,
    @MockK gatewayMock: BlameCalculatorLocalGateway,
    @RelaxedMockK gitLineHandlerMock: GitLineHandler
  ) {
    // given
    every { gatewayMock.getCurrentRevisionNumber(vFileMock) } returns GitRevisionNumber("abc")
    every { gatewayMock.createLineHandler(repoMock) } returns gitLineHandlerMock
    val commandResult = GitCommandResult(false, 0, listOf(), listOf())
    every { gatewayMock.runCommand(gitLineHandlerMock) } returns commandResult
    val calculator = BlameCalculator(projectMock, gatewayMock)

    // when
    val dataProvider = calculator.annotate(repoMock, vFileMock)

    // then
    assertThat(dataProvider).isNotNull
  }

  @Test
  internal fun annotateReturnsNullIfCurrentRevisionNumberPresentAndCommandFailed(
    @MockK projectMock: Project,
    @MockK repoMock: GitRepository,
    @MockK gatewayMock: BlameCalculatorLocalGateway,
    @RelaxedMockK gitLineHandlerMock: GitLineHandler
  ) {
    // given
    every { gatewayMock.getCurrentRevisionNumber(vFileMock) } returns GitRevisionNumber("abc")
    every { gatewayMock.createLineHandler(repoMock) } returns gitLineHandlerMock
    val commandResult = GitCommandResult(false, 1, listOf("Blame failure for test"), listOf())
    every { gatewayMock.runCommand(gitLineHandlerMock) } returns commandResult
    val calculator = BlameCalculator(projectMock, gatewayMock)

    // when
    val dataProvider = calculator.annotate(repoMock, vFileMock)

    // then
    assertThat(dataProvider).isNull()
  }
}