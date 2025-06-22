package ConnectedClient;

public interface DependentClient {
  void onDependencyLeave(ConnectedClient dependency);
}
