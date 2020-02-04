# Notes

Please add here any notes, assumptions and design decisions that might help up understand your though process.
## Design decisions
* PricingDiscount component is responsible for keeping and apply any discount policy set up for either items by unit or weight
* Any discount policy/functionality adheres to Discount contract via the Contract interface implementation.
* The basket is associated/linked with the supported/setup discount policies that will be apply in order to calculate discounts for either items by unit or weight

## Assumptions
* Discount: Buy two items by one pound will be only applicable when the sum of both is greater than one pound.

## Dependencies added in `pom.xml`
Lombok: To generate at compile time the override of `equals()` and `hashCode()` methods.
Apache Commons Lang 3: For data input checking like empty strings, null values, etc.


