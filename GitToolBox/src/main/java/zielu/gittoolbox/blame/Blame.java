package zielu.gittoolbox.blame;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Blame {
  @NotNull
  String getShortText();

  @Nullable
  String getDetailedText();
}