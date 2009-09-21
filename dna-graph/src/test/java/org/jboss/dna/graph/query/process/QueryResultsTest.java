/*
 * JBoss DNA (http://www.jboss.org/dna)
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * See the AUTHORS.txt file in the distribution for a full listing of 
 * individual contributors.
 *
 * JBoss DNA is free software. Unless otherwise indicated, all code in JBoss DNA
 * is licensed to you under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * JBoss DNA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.dna.graph.query.process;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.jboss.dna.graph.query.QueryResults.Columns;
import org.jboss.dna.graph.query.model.Column;
import org.jboss.dna.graph.query.process.QueryResultColumns;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class QueryResultsTest extends AbstractQueryResultsTest {

    private List<Column> columnList;
    private QueryResultColumns columnsWithoutScores;
    private QueryResultColumns columnsWithScores;

    @Before
    public void beforeEach() {
        columnList = new ArrayList<Column>();
        columnList.add(new Column(selector("table1"), name("colA"), "colA"));
        columnList.add(new Column(selector("table1"), name("colB"), "colB"));
        columnList.add(new Column(selector("table1"), name("colC"), "colC"));
        columnList.add(new Column(selector("table2"), name("colA"), "colA2"));
        columnList.add(new Column(selector("table2"), name("colB"), "colB2"));
        columnList.add(new Column(selector("table2"), name("colX"), "colX"));
        columnsWithoutScores = new QueryResultColumns(columnList, false);
        columnsWithScores = new QueryResultColumns(columnList, true);
    }

    @Test
    public void shouldHaveCorrectTupleSize() {
        assertThat(columnsWithScores.getTupleSize(), is(columnList.size() + 2 + 2));
        assertThat(columnsWithoutScores.getTupleSize(), is(columnList.size() + 2 + 0));
    }

    @Test
    public void shouldHaveCorrectTupleNames() {
        List<String> expected = new ArrayList<String>();
        expected.add("colA");
        expected.add("colB");
        expected.add("colC");
        expected.add("colA2");
        expected.add("colB2");
        expected.add("colX");
        expected.add("Location(table1)");
        expected.add("Location(table2)");
        assertThat(columnsWithoutScores.getTupleValueNames(), is(expected));
        expected.add("Score(table1)");
        expected.add("Score(table2)");
        assertThat(columnsWithScores.getTupleValueNames(), is(expected));
    }

    @Test
    public void shouldHaveCorrectSelectorNames() {
        List<String> expected = new ArrayList<String>();
        expected.add("table1");
        expected.add("table2");
        assertThat(columnsWithoutScores.getSelectorNames(), is(expected));
        assertThat(columnsWithScores.getSelectorNames(), is(expected));
    }

    @Test
    public void shouldHaveCorrectNumberOfColumns() {
        assertThat(columnsWithScores.getColumnCount(), is(columnList.size()));
        assertThat(columnsWithoutScores.getColumnCount(), is(columnList.size()));
    }

    @Test
    public void shouldReturnColumns() {
        assertThat(columnsWithScores.getColumns(), is(columnList));
        assertThat(columnsWithoutScores.getColumns(), is(columnList));
    }

    @Test( expected = UnsupportedOperationException.class )
    public void shouldReturnImmutableColumns() {
        assertThat(columnsWithoutScores.getColumns().isEmpty(), is(false));
        columnsWithoutScores.getColumns().clear();
    }

    @Test
    public void shouldReturnColumnNames() {
        List<String> names = new ArrayList<String>();
        for (Column column : columnList) {
            names.add(column.getColumnName());
        }
        assertThat(columnsWithScores.getColumnNames(), is(names));
        assertThat(columnsWithoutScores.getColumnNames(), is(names));
    }

    @Test
    public void shouldReturnCorrectIndexOfColumnGivenColumnName() {
        for (Column column : columnList) {
            assertThat(columnsWithoutScores.getColumnIndexForName(column.getColumnName()), is(columnList.indexOf(column)));
        }
    }

    @Test
    public void shouldReturnCorrectIndexOfColumnGivenColumnSelectorAndPropertyName() {
        for (Column column : columnList) {
            assertThat(columnsWithoutScores.getColumnIndexForProperty(column.getSelectorName().getName(),
                                                                      column.getPropertyName()), is(columnList.indexOf(column)));
        }
    }

    @Test( expected = NoSuchElementException.class )
    public void shouldFailToFindIndexOfColumnGivenColumnNameWithIncorrectCase() {
        columnsWithScores.getColumnIndexForName("cola");
    }

    @Test( expected = NoSuchElementException.class )
    public void shouldFailToFindIndexOfColumnGivenNonExistantColumnName() {
        columnsWithScores.getColumnIndexForName("non-existant");
    }

    @Test( expected = NoSuchElementException.class )
    public void shouldFailToFindIndexOfColumnGivenNullColumnName() {
        columnsWithScores.getColumnIndexForName(null);
    }

    @Test( expected = NoSuchElementException.class )
    public void shouldFailToFindIndexOfColumnGivenUnusedSelectorName() {
        columnsWithScores.getColumnIndexForProperty("non-existant", name("colA"));
    }

    @Test( expected = NoSuchElementException.class )
    public void shouldFailToFindIndexOfColumnGivenNullSelectorName() {
        columnsWithScores.getColumnIndexForProperty(null, name("colA"));
    }

    @Test( expected = NoSuchElementException.class )
    public void shouldFailToFindIndexOfColumnGivenEmptySelectorName() {
        columnsWithScores.getColumnIndexForProperty("", name("colA"));
    }

    @Test( expected = NoSuchElementException.class )
    public void shouldFailToFindIndexOfColumnGivenUnusedPropertyNameName() {
        columnsWithScores.getColumnIndexForProperty("table1", name("non-existant"));
    }

    @Test( expected = NoSuchElementException.class )
    public void shouldFailToFindIndexOfColumnGivenNullPropertyNameName() {
        columnsWithScores.getColumnIndexForProperty("table1", null);
    }

    @Test
    public void shouldHaveCorrectNumberOfLocations() {
        assertThat(columnsWithScores.getLocationCount(), is(2));
        assertThat(columnsWithoutScores.getLocationCount(), is(2));
    }

    @Test
    public void shouldReturnCorrectIndexOfLocationGivenSelectorName() {
        assertThat(columnsWithoutScores.getLocationIndex("table1"), is(columnList.size() + 0));
        assertThat(columnsWithoutScores.getLocationIndex("table2"), is(columnList.size() + 1));
    }

    @Test( expected = NoSuchElementException.class )
    public void shouldFailToFindIndexOfLocationGivenUnusedSelectorName() {
        columnsWithScores.getLocationIndex("non-existant");
    }

    @Test( expected = NoSuchElementException.class )
    public void shouldFailToFindIndexOfLocationGivenNullSelectorName() {
        columnsWithScores.getLocationIndex(null);
    }

    @Test( expected = NoSuchElementException.class )
    public void shouldFailToFindIndexOfLocationGivenEmptySelectorName() {
        columnsWithScores.getLocationIndex("");
    }

    @Test
    public void shouldReturnCorrectIndexOfLocationGivenColumnName() {
        assertThat(columnsWithoutScores.getLocationIndexForColumn("colA"), is(columnList.size() + 0));
        assertThat(columnsWithoutScores.getLocationIndexForColumn("colB"), is(columnList.size() + 0));
        assertThat(columnsWithoutScores.getLocationIndexForColumn("colC"), is(columnList.size() + 0));
        assertThat(columnsWithoutScores.getLocationIndexForColumn("colA2"), is(columnList.size() + 1));
        assertThat(columnsWithoutScores.getLocationIndexForColumn("colB2"), is(columnList.size() + 1));
        assertThat(columnsWithoutScores.getLocationIndexForColumn("colX"), is(columnList.size() + 1));
    }

    @Test( expected = NoSuchElementException.class )
    public void shouldFailToFindIndexOfLocationGivenUnusedColumnName() {
        columnsWithScores.getLocationIndexForColumn("non-existant");
    }

    @Test( expected = NoSuchElementException.class )
    public void shouldFailToFindIndexOfLocationGivenNullColumnName() {
        columnsWithScores.getLocationIndexForColumn(null);
    }

    @Test( expected = NoSuchElementException.class )
    public void shouldFailToFindIndexOfLocationGivenEmptyColumnName() {
        columnsWithScores.getLocationIndexForColumn("");
    }

    @Test
    public void shouldReturnCorrectIndexOfLocationGivenColumnIndex() {
        assertThat(columnsWithoutScores.getLocationIndexForColumn(0), is(columnList.size() + 0));
        assertThat(columnsWithoutScores.getLocationIndexForColumn(1), is(columnList.size() + 0));
        assertThat(columnsWithoutScores.getLocationIndexForColumn(2), is(columnList.size() + 0));
        assertThat(columnsWithoutScores.getLocationIndexForColumn(3), is(columnList.size() + 1));
        assertThat(columnsWithoutScores.getLocationIndexForColumn(4), is(columnList.size() + 1));
        assertThat(columnsWithoutScores.getLocationIndexForColumn(5), is(columnList.size() + 1));
    }

    @Test( expected = IndexOutOfBoundsException.class )
    public void shouldFailToFindIndexOfLocationGivenColumnIndexEqualToOrLargerThanNumberOfColumns() {
        columnsWithScores.getLocationIndexForColumn(columnList.size());
    }

    @Test( expected = IndexOutOfBoundsException.class )
    public void shouldFailToFindIndexOfLocationGivenColumnIndexLessThanZero() {
        columnsWithScores.getLocationIndexForColumn(-1);
    }

    @Test
    public void shouldCorrectlyReportWhetherScoresAreIncluded() {
        assertThat(columnsWithScores.hasFullTextSearchScores(), is(true));
        assertThat(columnsWithoutScores.hasFullTextSearchScores(), is(false));
    }

    @Test
    public void shouldReturnCorrectIndexOfFullTextSearchScoreGivenSelectorName() {
        assertThat(columnsWithScores.getFullTextSearchScoreIndexFor("table1"), is(columnList.size() + 2 + 0));
        assertThat(columnsWithScores.getFullTextSearchScoreIndexFor("table2"), is(columnList.size() + 2 + 1));
    }

    @Test
    public void shouldReturnNegativeOneForIndexOfFullTextSearchScoreGivenValidSelectorNameButWhereNoScoresAreIncluded() {
        assertThat(columnsWithoutScores.getFullTextSearchScoreIndexFor("table1"), is(-1));
        assertThat(columnsWithoutScores.getFullTextSearchScoreIndexFor("table2"), is(-1));
    }

    @Test( expected = NoSuchElementException.class )
    public void shouldFailToFindIndexOfFullTextSearchScoreGivenUnusedSelectorName() {
        columnsWithScores.getFullTextSearchScoreIndexFor("non-existant");
    }

    @Test( expected = NoSuchElementException.class )
    public void shouldFailToFindIndexOfFullTextSearchScoreGivenNullSelectorName() {
        columnsWithScores.getFullTextSearchScoreIndexFor(null);
    }

    @Test( expected = NoSuchElementException.class )
    public void shouldFailToFindIndexOfFullTextSearchScoreGivenEmptySelectorName() {
        columnsWithScores.getFullTextSearchScoreIndexFor("");
    }

    @Test
    public void shouldIncludeSelf() {
        assertThat(columnsWithScores.includes(columnsWithScores), is(true));
        assertThat(columnsWithoutScores.includes(columnsWithoutScores), is(true));
    }

    @Test
    public void shouldIncludeColumnsObjectWithSubsetOfColumnObjectsAndIndependentOfFullTextSearchScores() {
        List<Column> subset = new ArrayList<Column>();
        subset.add(columnList.get(0));
        subset.add(columnList.get(1));
        subset.add(columnList.get(4));
        Columns other = new QueryResultColumns(subset, false);
        assertThat(columnsWithScores.includes(other), is(true));
        assertThat(columnsWithoutScores.includes(other), is(true));
        assertThat(columnsWithoutScores.includes(columnsWithScores), is(true));
    }

    @Test
    public void shouldEqualSelf() {
        assertThat(columnsWithScores.equals(columnsWithScores), is(true));
        assertThat(columnsWithoutScores.equals(columnsWithoutScores), is(true));
    }

    @Test
    public void shouldEqualIndependentOfInclusionOfFullTextSearchScores() {
        assertThat(columnsWithoutScores.equals(columnsWithScores), is(true));
    }

    @Test
    public void shouldNotBeUnionCompatibleUnlessBothHaveFullTextSearchScores() {
        Columns other = new QueryResultColumns(columnsWithoutScores.getColumns(), !columnsWithoutScores.hasFullTextSearchScores());
        assertThat(columnsWithoutScores.isUnionCompatible(other), is(false));
    }

    @Test
    public void shouldNotBeUnionCompatibleUnlessBothDoNotHaveFullTextSearchScores() {
        Columns other = new QueryResultColumns(columnsWithScores.getColumns(), !columnsWithScores.hasFullTextSearchScores());
        assertThat(columnsWithScores.isUnionCompatible(other), is(false));
    }

    @Test
    public void shouldBeUnionCompatibleWithEquivalentColumns() {
        List<Column> columnListCopy = new ArrayList<Column>();
        for (Column column : columnsWithScores.getColumns()) {
            columnListCopy.add(new Column(column.getSelectorName(), column.getPropertyName(), column.getColumnName()));
        }
        Columns other = new QueryResultColumns(columnListCopy, columnsWithScores.hasFullTextSearchScores());
        assertThat(columnsWithScores.isUnionCompatible(other), is(true));
    }

    @Test
    public void shouldNotBeUnionCompatibleWithSubsetOfColumns() {
        List<Column> columnListCopy = new ArrayList<Column>();
        for (Column column : columnsWithScores.getColumns()) {
            columnListCopy.add(new Column(column.getSelectorName(), column.getPropertyName(), column.getColumnName()));
        }
        columnListCopy.remove(3);
        Columns other = new QueryResultColumns(columnListCopy, columnsWithScores.hasFullTextSearchScores());
        assertThat(columnsWithScores.isUnionCompatible(other), is(false));
    }

    @Test
    public void shouldNotBeUnionCompatibleWithExtraColumns() {
        List<Column> columnListCopy = new ArrayList<Column>();
        for (Column column : columnsWithScores.getColumns()) {
            columnListCopy.add(new Column(column.getSelectorName(), column.getPropertyName(), column.getColumnName()));
        }
        columnListCopy.add(new Column(selector("table2"), name("colZ"), "colZ"));
        Columns other = new QueryResultColumns(columnListCopy, columnsWithScores.hasFullTextSearchScores());
        assertThat(columnsWithScores.isUnionCompatible(other), is(false));
    }

    @Test
    public void shouldBeUnionCompatibleWithSameColumns() {
        Columns other = new QueryResultColumns(columnsWithScores.getColumns(), columnsWithScores.hasFullTextSearchScores());
        assertThat(columnsWithScores.isUnionCompatible(other), is(true));
    }

    @Test
    public void shouldHaveToString() {
        columnsWithScores.toString();
        columnsWithoutScores.toString();
    }
}
