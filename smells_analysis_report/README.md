
# Software Smells Analysis (Final Version)

This repository provides a comprehensive analysis of software smells identified in the project, categorized into three main areas:
- **Architecture Smells**
- **Design Smells**
- **Implementation Smells**

## Purpose

Software smells are indicators of potential code quality issues that may lead to maintainability or scalability challenges. This analysis identifies, resolves, or justifies unresolved smells for further refactoring.


## Final Analysis Overview

### **1. Architecture Smells**
- **Total Detected**: 5
- **Resolved**: 0
- **False Positives**: 0
- **Unresolved**: 5

### **2. Design Smells**
- **Total Detected**: 57
- **Resolved**: 45
- **False Positives**: 0
- **Unresolved**: 12

### **3. Implementation Smells**
- **Total Detected**: 60
- **Resolved**: 57
- **False Positives**: 0
- **Unresolved**: 3


## Files in the Repository

1. **Architecture_Smells.csv**  
   - Documents architecture smells, such as:
     - Feature Concentration
     - Unstable Dependency
   - Provides details about resolved, false positive, and unresolved smells.

2. **Design_Smells.csv**  
   - Captures design-related smells, such as:
     - Feature Envy
     - Unutilized Abstraction
   - Includes resolutions and justifications for unresolved smells.

3. **Implementation_Smells.csv**  
   - Lists implementation smells like:
     - Long Methods
     - Complex Statements
   - Resolution details are provided for solved cases.


## Quality Insights

### Design Principles Followed:
1. **Single Responsibility Principle (SRP)**: Ensured each class has a focused responsibility.
2. **Open/Closed Principle (OCP)**: Extended functionality using child classes.
3. **Liskov Substitution Principle (LSP)**: Maintained substitutability between parent and child classes.
4. **Interface Segregation Principle (ISP)**: Created task-specific smaller interfaces.
5. **Dependency Inversion Principle (DIP)**: Employed interfaces for dependency abstraction.

### Resolutions Implemented:
- **Architecture**: Addressed Feature Concentration by restructuring components.
- **Design**: Removed unused abstractions and simplified complex relationships.
- **Implementation**: Refactored long statements and complex methods for better readability.

