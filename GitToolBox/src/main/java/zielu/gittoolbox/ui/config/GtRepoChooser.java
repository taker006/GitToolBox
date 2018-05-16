package zielu.gittoolbox.ui.config;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import git4idea.repo.GitRepository;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zielu.gittoolbox.ResBundle;
import zielu.gittoolbox.ui.common.RepoListCellRenderer;
import zielu.gittoolbox.util.GtUtil;

class GtRepoChooser extends DialogWrapper {
  private final Project project;
  private JPanel centerPanel;
  private JBList<GitRepository> repoList;
  private List<GitRepository> repositories = new ArrayList<>();
  private List<GitRepository> selectedRepositories = new ArrayList<>();

  GtRepoChooser(@NotNull Project project, @Nullable Component parentComponent) {
    super(project, parentComponent, false, IdeModalityType.PROJECT);
    this.project = project;
    centerPanel = new JPanel(new BorderLayout());
    repoList = new JBList<>();
    repoList.setCellRenderer(new RepoListCellRenderer());
    JBScrollPane scrollPane = new JBScrollPane(repoList);
    centerPanel.add(scrollPane, BorderLayout.CENTER);
    setTitle(ResBundle.getString("configurable.prj.autoFetch.exclusions.add.title"));
    init();
  }

  private void fillData() {
    List<GitRepository> repositoriesToShow = new ArrayList<>(repositories);
    repositoriesToShow.removeAll(selectedRepositories);
    repositoriesToShow = GtUtil.sort(repositoriesToShow);
    repoList.setModel(new CollectionListModel<>(repositoriesToShow));
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    return centerPanel;
  }

  public List<GitRepository> getSelectedRepositories() {
    return selectedRepositories;
  }

  public void setSelectedRepositories(List<GitRepository> repositories) {
    selectedRepositories = new ArrayList<>(repositories);
  }

  public void setRepositories(List<GitRepository> repositories) {
    this.repositories = new ArrayList<>(repositories);
  }

  @Override
  public void show() {
    fillData();
    super.show();
  }

  @Override
  protected void doOKAction() {
    selectedRepositories = repoList.getSelectedValuesList();
    super.doOKAction();
  }
}
