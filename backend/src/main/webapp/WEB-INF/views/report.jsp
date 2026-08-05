<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Game Transaction Report</title>

                <!-- Link to the external CSS file using JSTL core tag for safe context routing -->
                <link href="<c:url value='/resources/css/style.css' />" rel="stylesheet" type="text/css">
            </head>

            <body>

                <div class="container">
                    <h1>Game Transaction Report</h1>

                    <c:if test="${not empty error}">
                        <div class="error-message">${error}</div>
                    </c:if>

                    <!-- Main Search Form -->
                    <form action="/report" method="get" id="searchForm">
                        <div class="search-form">
                            <div class="form-group">
                                <label for="startDate">Start Date/Time *</label>
                                <input type="datetime-local" id="startDate" name="startDate" value="${param.startDate}"
                                    required>
                            </div>
                            <div class="form-group">
                                <label for="endDate">End Date/Time *</label>
                                <input type="datetime-local" id="endDate" name="endDate" value="${param.endDate}"
                                    required>
                            </div>
                            <div class="form-group">
                                <label for="accountId">Account ID (Optional)</label>
                                <input type="text" id="accountId" name="accountId" value="${param.accountId}"
                                    placeholder="Enter Account ID">
                            </div>
                            <div class="form-group">
                                <label for="size">Page Size</label>
                                <select id="size" name="size">
                                    <option value="25" ${currentSize==25 ? 'selected' : '' }>25</option>
                                    <option value="50" ${currentSize==50 ? 'selected' : '' }>50</option>
                                </select>
                            </div>
                            <div class="form-group" style="justify-content: flex-end;">
                                <button type="submit" class="btn">Generate Report</button>
                            </div>
                        </div>

                        <!-- Hidden fields for sorting state -->
                        <input type="hidden" name="sortCol" value="${sortCol}">
                        <input type="hidden" name="sortDir" value="${sortDir}">
                        <!-- Hidden fields for filter state retention -->
                        <input type="hidden" name="platformTranId" value="${param.platformTranId}">
                        <input type="hidden" name="gameTranId" value="${param.gameTranId}">
                        <input type="hidden" name="gameId" value="${param.gameId}">
                        <input type="hidden" name="tranType" value="${param.tranType}">
                    </form>

                    <!-- This is when the search is initiated by the user -->
                    <c:if test="${searchInitiated}">
                        <div class="header-actions">
                            <h2>Transaction Results</h2>
                            <c:if test="${not empty transactions}">
                                <!-- CSV Export Button -->
                                <a href="/report/export?startDate=${param.startDate}&endDate=${param.endDate}&accountId=${param.accountId}&platformTranId=${param.platformTranId}&gameTranId=${param.gameTranId}&gameId=${param.gameId}&tranType=${param.tranType}"
                                    class="btn btn-success">Export to CSV</a>
                            </c:if>
                        </div>

                        <c:if test="${empty transactions}">
                            <div class="info-message">
                                No transaction records found for the selected date range and filter criteria.
                                <br><small><em>Note: Sample database records are dated between <strong>July
                                            2025</strong> and <strong>December 2025</strong> (e.g.,
                                        <code>2025-07-31</code> to <code>2025-12-24</code>).</em></small>
                            </div>
                        </c:if>

                        <c:if test="${not empty summary}">
                            <!-- Summary Section -->
                            <div class="summary-cards">
                                <div class="card">
                                    <h4>Total Bets</h4>
                                    <div class="value">
                                        <fmt:formatNumber value="${summary.betSum}" type="currency"
                                            currencySymbol="$" />
                                    </div>
                                </div>
                                <div class="card">
                                    <h4>Total Wins</h4>
                                    <div class="value">
                                        <fmt:formatNumber value="${summary.winSum}" type="currency"
                                            currencySymbol="$" />
                                    </div>
                                </div>
                                <div class="card">
                                    <h4>Net (Win - Bet)</h4>
                                    <div class="value"
                                        style="color: ${summary.net >= 0 ? 'var(--success-color)' : 'var(--error-color)'};">
                                        <fmt:formatNumber value="${summary.net}" type="currency" currencySymbol="$" />
                                    </div>
                                </div>
                            </div>
                        </c:if>

                        <!-- Report Table -->
                        <div class="table-responsive">
                            <!-- Inline form for column-specific filtering -->
                            <form action="/report" method="get">
                                <!-- Retain global scope values -->
                                <input type="hidden" name="startDate" value="${param.startDate}">
                                <input type="hidden" name="endDate" value="${param.endDate}">
                                <input type="hidden" name="size" value="${currentSize}">
                                <input type="hidden" name="sortCol" value="${sortCol}">
                                <input type="hidden" name="sortDir" value="${sortDir}">

                                <table>
                                    <thead>
                                        <!-- Sortable Headers -->
                                        <tr>
                                            <th>
                                                <a
                                                    href="?startDate=${param.startDate}&endDate=${param.endDate}&size=${currentSize}&accountId=${param.accountId}&platformTranId=${param.platformTranId}&gameTranId=${param.gameTranId}&gameId=${param.gameId}&tranType=${param.tranType}&sortCol=id&sortDir=${sortCol == 'id' && sortDir == 'asc' ? 'desc' : 'asc'}">
                                                    ID ${sortCol == 'id' ? (sortDir == 'asc' ? '▲' : '▼') : ''}
                                                </a>
                                            </th>
                                            <th>
                                                <a
                                                    href="?startDate=${param.startDate}&endDate=${param.endDate}&size=${currentSize}&accountId=${param.accountId}&platformTranId=${param.platformTranId}&gameTranId=${param.gameTranId}&gameId=${param.gameId}&tranType=${param.tranType}&sortCol=accountId&sortDir=${sortCol == 'accountId' && sortDir == 'asc' ? 'desc' : 'asc'}">
                                                    Account ID ${sortCol == 'accountId' ? (sortDir == 'asc' ? '▲' : '▼')
                                                    : ''}
                                                </a>
                                            </th>
                                            <th>
                                                <a
                                                    href="?startDate=${param.startDate}&endDate=${param.endDate}&size=${currentSize}&accountId=${param.accountId}&platformTranId=${param.platformTranId}&gameTranId=${param.gameTranId}&gameId=${param.gameId}&tranType=${param.tranType}&sortCol=datetime&sortDir=${sortCol == 'datetime' && sortDir == 'asc' ? 'desc' : 'asc'}">
                                                    Datetime ${sortCol == 'datetime' ? (sortDir == 'asc' ? '▲' : '▼') :
                                                    ''}
                                                </a>
                                            </th>
                                            <th>
                                                <a
                                                    href="?startDate=${param.startDate}&endDate=${param.endDate}&size=${currentSize}&accountId=${param.accountId}&platformTranId=${param.platformTranId}&gameTranId=${param.gameTranId}&gameId=${param.gameId}&tranType=${param.tranType}&sortCol=tranType&sortDir=${sortCol == 'tranType' && sortDir == 'asc' ? 'desc' : 'asc'}">
                                                    Tran Type ${sortCol == 'tranType' ? (sortDir == 'asc' ? '▲' : '▼') :
                                                    ''}
                                                </a>
                                            </th>
                                            <th>
                                                <a
                                                    href="?startDate=${param.startDate}&endDate=${param.endDate}&size=${currentSize}&accountId=${param.accountId}&platformTranId=${param.platformTranId}&gameTranId=${param.gameTranId}&gameId=${param.gameId}&tranType=${param.tranType}&sortCol=platformTranId&sortDir=${sortCol == 'platformTranId' && sortDir == 'asc' ? 'desc' : 'asc'}">
                                                    Platform Tran ID ${sortCol == 'platformTranId' ? (sortDir == 'asc' ?
                                                    '▲' : '▼') : ''}
                                                </a>
                                            </th>
                                            <th>
                                                <a
                                                    href="?startDate=${param.startDate}&endDate=${param.endDate}&size=${currentSize}&accountId=${param.accountId}&platformTranId=${param.platformTranId}&gameTranId=${param.gameTranId}&gameId=${param.gameId}&tranType=${param.tranType}&sortCol=gameTranId&sortDir=${sortCol == 'gameTranId' && sortDir == 'asc' ? 'desc' : 'asc'}">
                                                    Game Tran ID ${sortCol == 'gameTranId' ? (sortDir == 'asc' ? '▲' :
                                                    '▼') : ''}
                                                </a>
                                            </th>
                                            <th>
                                                <a
                                                    href="?startDate=${param.startDate}&endDate=${param.endDate}&size=${currentSize}&accountId=${param.accountId}&platformTranId=${param.platformTranId}&gameTranId=${param.gameTranId}&gameId=${param.gameId}&tranType=${param.tranType}&sortCol=gameId&sortDir=${sortCol == 'gameId' && sortDir == 'asc' ? 'desc' : 'asc'}">
                                                    Game ID ${sortCol == 'gameId' ? (sortDir == 'asc' ? '▲' : '▼') : ''}
                                                </a>
                                            </th>
                                            <th>Amount</th>
                                            <th>Balance</th>
                                        </tr>
                                        <!-- Filter Input Row -->
                                        <tr class="filter-row">
                                            <td></td>
                                            <td><input type="text" name="accountId" value="${param.accountId}"
                                                    placeholder="Filter Account"></td>
                                            <td></td>
                                            <td><input type="text" name="tranType" value="${param.tranType}"
                                                    placeholder="Filter Type"></td>
                                            <td><input type="text" name="platformTranId" value="${param.platformTranId}"
                                                    placeholder="Filter Platform"></td>
                                            <td><input type="text" name="gameTranId" value="${param.gameTranId}"
                                                    placeholder="Filter Game Tran"></td>
                                            <td><input type="text" name="gameId" value="${param.gameId}"
                                                    placeholder="Filter Game"></td>
                                            <td colspan="2"><button type="submit" class="btn"
                                                    style="width: 100%; padding: 6px;">Apply Filters</button></td>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:if test="${empty transactions}">
                                            <tr>
                                                <td colspan="9" style="text-align: center; padding: 25px; color: #777;">
                                                    No matching transactions found. Try adjusting your date range or
                                                    filters.
                                                </td>
                                            </tr>
                                        </c:if>
                                        <c:forEach var="txn" items="${transactions}">
                                            <tr>
                                                <td>${txn.id}</td>
                                                <td>${txn.accountId}</td>
                                                <td>${txn.datetime}</td>
                                                <td>${txn.tranType}</td>
                                                <td>${txn.platformTranId}</td>
                                                <td>${txn.gameTranId}</td>
                                                <td>${txn.gameId}</td>
                                                <td>
                                                    <fmt:formatNumber value="${txn.totalAmount}" type="currency"
                                                        currencySymbol="$" />
                                                </td>
                                                <td>
                                                    <fmt:formatNumber value="${txn.totalBalance}" type="currency"
                                                        currencySymbol="$" />
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </form>
                        </div>

                        <c:if test="${not empty transactions}">
                            <!-- Pagination Controls -->
                            <div class="pagination-container">
                                <div class="pagination-info">
                                    Showing Page ${currentPage + 1} of ${totalPages} (Total Records: ${totalElements})
                                </div>
                                <div class="pagination-controls">
                                    <c:if test="${currentPage > 0}">
                                        <a href="?startDate=${param.startDate}&endDate=${param.endDate}&size=${currentSize}&accountId=${param.accountId}&platformTranId=${param.platformTranId}&gameTranId=${param.gameTranId}&gameId=${param.gameId}&tranType=${param.tranType}&sortCol=${sortCol}&sortDir=${sortDir}&page=${currentPage - 1}"
                                            class="btn">Previous</a>
                                    </c:if>

                                    <c:if test="${currentPage + 1 < totalPages}">
                                        <a href="?startDate=${param.startDate}&endDate=${param.endDate}&size=${currentSize}&accountId=${param.accountId}&platformTranId=${param.platformTranId}&gameTranId=${param.gameTranId}&gameId=${param.gameId}&tranType=${param.tranType}&sortCol=${sortCol}&sortDir=${sortDir}&page=${currentPage + 1}"
                                            class="btn">Next</a>
                                    </c:if>
                                </div>
                            </div>
                        </c:if>
                    </c:if>
                </div>

            </body>

            </html>