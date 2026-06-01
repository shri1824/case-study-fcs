# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```
**Answer:**

I noticed that the application uses different approaches for database access. The `Store` and `Product` modules use Panache entities directly, while the `Warehouse` module uses a cleaner structure with separate operations and data access layers.

If I were maintaining this project, I would gradually move all modules toward the Warehouse approach because:

* It keeps business logic separate from database code.
* It is easier to test.
* It is easier to maintain and extend in the future.
* It provides a more consistent structure across the application.

For a small application, the current Panache approach is simple and works well, so I would not refactor everything immediately. I would improve it gradually as new features are added.


```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```
Both approaches have advantages.

OpenAPI-first (Warehouse API):

Clear API contract.
Documentation is available automatically.
Easier for other teams to integrate with the API.
Reduces misunderstandings about request and response formats.

Code-first (Store and Product APIs):

Faster to develop.
Simpler for small projects.
Less setup and configuration.

My preference would be OpenAPI-first for production systems because it keeps the API contract, documentation, and implementation aligned. For small internal projects, code-first can still be a good option because it is quicker to implement

```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```
**Answer:**

Because time and resources are limited, I would focus on the tests that provide the most value.

My priorities would be:

1. **Unit Tests**

   * Test business rules and validation logic.
   * Fast and easy to maintain.

2. **Integration/API Tests**

   * Test REST endpoints.
   * Verify database interactions.
   * Ensure the application works correctly from end to end.

3. **End-to-End Tests**

   * Only for the most important business flows.

To keep test coverage effective over time:

* Run all tests automatically in the CI/CD pipeline.
* Use JaCoCo to track coverage and keep it above 80%.
* Add tests whenever a new feature or bug fix is implemented.
* Focus on testing business logic instead of simple data classes or generated code.

The goal is not only to achieve high coverage but also to ensure that the most important application functionality is well tested.

```