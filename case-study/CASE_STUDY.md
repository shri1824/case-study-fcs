# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**
[ fill here your answer ]
I would first clarify how the company currently defines cost centers. 
For example:
    Is each Warehouse considered an independent cost center?
    Are Stores responsible for transportation costs or only inventory-related costs?
    Are costs allocated by volume, weight, order count, or revenue?

Another important consideration is historical traceability. Since Warehouses can be replaced while keeping the same Business Unit Code, the system must preserve operational and financial history without mixing old and new operational data incorrectly.
    I would also ask:
    Which systems are the source of truth for financial data?
    Are costs captured in real time or through batch processes?
    Are there audit or compliance requirements?
From previous experiences in distributed systems and operational platforms, one recurring issue is data inconsistency between operational and financial systems. For example, inventory movement may happen in one system while financial posting happens later in another. This can create reconciliation problems if events are not traceable.
To address this, I would consider:
    Event-driven tracking for inventory and transportation operations
    Immutable historical records for archived Warehouses
    Clear ownership of financial calculations
    Versioning or effective dates for Warehouse lifecycle changes

## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**
[ fill here your answer ]
Cost optimization should balance operational efficiency and service quality. Reducing costs without understanding operational impact can negatively affect delivery time, inventory availability, or customer satisfaction.
I would start by identifying the highest operational cost drivers, such as:
    Transportation routes
    Inventory storage
    Warehouse utilization
    Labor allocation

Questions I would ask include:
    Which Warehouses have the highest operational costs?
    Are there underutilized Warehouses?
    What are the main causes of inventory transfers or delays?
    Are transportation costs increasing due to inefficient routing?
Potential optimization strategies could include:
    Reducing unnecessary stock transfers
    Automating repetitive operational processes
    Improving demand forecasting
    Consolidating deliveries

## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**
[ fill here your answer ]
The integration should support real-time data synchronization and reporting
Main benefits include:
    Real-time visibility into operational costs
    Faster financial reconciliation
    Better budgeting and forecasting
I would first identify:
    Which financial systems are currently used
    Available APIs or integration mechanisms
    Existing data synchronization limitations
Important technical considerations:
    Idempotent integrations to avoid duplicated financial records
    Event-driven architecture for operational updates
    Retry and error handling strategies
    Monitoring and reconciliation mechanisms
To ensure seamless synchronization, I would consider:
    Using integration events for Warehouse lifecycle changes
    Maintaining immutable historical records
    Including timestamps and versioning in financial events
    Implementing reconciliation jobs between systems

## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**
[ fill here your answer ]
Budgeting and forecasting are important because fulfillment operations are highly influenced by variables such as demand fluctuations, transportation costs, inventory turnover, and seasonal trends.
Accurate forecasting helps the company:
    Plan Warehouse capacity
    Allocate labor efficiently
    Anticipate transportation costs
    Reduce operational waste

I would ask:
    What historical operational data is available?
    Are there seasonal demand patterns?
    How often are forecasts recalculated?
    What forecasting accuracy is expected?

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**
[ fill here your answer ]
Preserving cost history during Warehouse replacement is critical for operational continuity, financial traceability, and strategic analysis.
Since the Business Unit Code represents the business identity of the operational area, historical records must remain associated with that business unit even after the physical Warehouse changes.
Important considerations include:
    Separation between operational identity and physical infrastructure
    Historical auditability
    Cost comparison before and after replacement
    Transition cost visibility

Questions I would ask:
    What operational costs are expected during migration?
    Will both Warehouses operate simultaneously during transition?
    How long should archived Warehouse data remain accessible?
Cost control during replacement is important because transition periods often introduce temporary inefficiencies such as:
    Duplicate labor costs
    Inventory transfer expenses
    Transportation disruptions
Preserving historical data allows the company to:
    Compare operational efficiency between old and new Warehouses
    Evaluate whether replacement objectives were achieved
    Improve future replacement strategies
From a system design perspective, I would avoid overwriting Warehouse records. Instead:
    The old Warehouse should be archived
    The new Warehouse should be created as a new entity/version
    Both should remain linked through the Business Unit Code
    Historical costs should remain immutable
I would also consider implementing:
    Effective start/end dates
    Warehouse status tracking
    Historical reporting views
    Budget comparison dashboards before and after replacement
## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.
