package zielu.gittoolbox.ui.projectView;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ProjectViewNodeDecorator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.packageDependencies.ui.PackageDependenciesNode;
import com.intellij.ui.ColoredTreeCellRenderer;
import git4idea.repo.GitRepository;
import zielu.gittoolbox.GitToolBoxProject;
import zielu.gittoolbox.cache.PerRepoInfoCache;
import zielu.gittoolbox.config.GitToolBoxConfig;
import zielu.gittoolbox.status.GitAheadBehindCount;
import zielu.gittoolbox.ui.projectView.node.DecorableNode;
import zielu.gittoolbox.ui.projectView.node.DecorableNodeFactory;
import zielu.gittoolbox.util.LogWatch;

public class ProjectViewDecorator implements ProjectViewNodeDecorator {
    private final Logger LOG = Logger.getInstance(getClass());

    private final NodeDecorationFactory decorationFactory = NodeDecorationFactory.getInstance();
    private final DecorableNodeFactory decorableNodeFactory = new DecorableNodeFactory();

    @Override
    public void decorate(ProjectViewNode projectViewNode, PresentationData presentation) {
        LogWatch decorateWatch = LogWatch.createStarted(LOG, "Decorate");
        GitToolBoxConfig config = GitToolBoxConfig.getInstance();
        Project project = projectViewNode.getProject();
        if (config.showProjectViewStatus && project != null) {
            final boolean debug = LOG.isDebugEnabled();
            LogWatch decorationCheckWatch = LogWatch.createStarted(LOG, "Decoration check");
            DecorableNode decorableNode = decorableNodeFactory.nodeFor(projectViewNode);
            decorationCheckWatch.elapsed("[", projectViewNode.getName(), "]").finish();
            if (decorableNode != null)  {
                GitRepository repo = decorableNode.getRepo();
                if (repo != null) {
                    applyDecoration(project, repo, projectViewNode, presentation);
                    decorateWatch.elapsed("Decoration ", "[", projectViewNode.getName() + "]");
                } else {
                    if (debug) {
                        LOG.debug("No git repo: " + projectViewNode);
                    }
                }
            } else {
                if (debug) {
                    LOG.debug("Not decorable node: " + projectViewNode);
                }
            }
        }
        decorateWatch.finish();
    }

    private void applyDecoration(Project project, GitRepository repo, ProjectViewNode projectViewNode, PresentationData presentation) {
        final LogWatch decorateApplyWatch = LogWatch.createStarted(LOG, "Decorate apply");
        PerRepoInfoCache cache = GitToolBoxProject.getInstance(project).perRepoStatusCache();
        GitAheadBehindCount countOptional = cache.getInfo(repo).count;
        NodeDecoration decoration = decorationFactory.decorationFor(repo, countOptional);
        boolean applied = decoration.apply(projectViewNode, presentation);
        decorateApplyWatch.elapsed("for ", repo).finish();
        presentation.setChanged(applied);
    }

    @Override
    public void decorate(PackageDependenciesNode packageDependenciesNode, ColoredTreeCellRenderer coloredTreeCellRenderer) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Decorate package dependencies");
        }
    }
}
