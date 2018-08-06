package pact.producer.exception;

import java.util.Objects;

public class ErrorWrapper {

  private String error;

  public ErrorWrapper(String error) {
    this.error = error;
  }

  public String getError() {
    return error;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ErrorWrapper that = (ErrorWrapper) o;
    return Objects.equals(error, that.error);
  }

  @Override
  public int hashCode() {

    return Objects.hash(error);
  }

  @Override
  public String toString() {
    return "ErrorWrapper{" +
        "error='" + error + '\'' +
        '}';
  }
}
