class BaseError extends Error {
  constructor(name, message, errorContext) {
    super(message);
    this.name = name;
    this.errorContext = errorContext;
  }
}

class DBError extends BaseError {
  constructor(dbname, message, errorContext) {
    const name = 'DB Error: ' + dbname;
    super(name, message, errorContext);
  }
}

class ServiceError extends BaseError {
  constructor(servicename, message, errorContext) {
    const name = 'Service Error: ' + servicename;
    super(name, message, errorContext);
  }
}
