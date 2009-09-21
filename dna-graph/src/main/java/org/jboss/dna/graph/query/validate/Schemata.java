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
package org.jboss.dna.graph.query.validate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jboss.dna.graph.property.PropertyType;
import org.jboss.dna.graph.query.model.SelectorName;

/**
 * The interface used to access the structure being queried and validate a query.
 */
public interface Schemata {

    /**
     * Get the information for the table with the supplied name within this schema.
     * 
     * @param name the table name; may not be null
     * @return the table information, or null if there is no such table
     */
    Table getTable( SelectorName name );

    /**
     * Information about a queryable table.
     */
    public interface Table {
        /**
         * Get the name for this table.
         * 
         * @return the table name; never null
         */
        SelectorName getName();

        /**
         * Get the information for a column with the supplied name within this table.
         * 
         * @param name the column name; may not be null
         * @return the column information, or null if there is no such column
         */
        Column getColumn( String name );

        /**
         * Get the queryable columns in this table.
         * 
         * @return the map of column objects by their name; never null
         */
        Map<String, Column> getColumnsByName();

        /**
         * Get the queryable columns in this table.
         * 
         * @return the ordered column objects; never null
         */
        List<Column> getColumns();

        /**
         * Get the collection of keys for this table.
         * 
         * @return the immutable collection of keys; never null, but possibly empty
         */
        Collection<Key> getKeys();

        /**
         * Determine whether this table has a {@link #getKeys() key} that contains exactly those columns listed.
         * 
         * @param columns the columns for the key
         * @return true if this table contains a key using exactly the supplied columns, or false otherwise
         */
        boolean hasKey( Column... columns );

        /**
         * Determine whether this table has a {@link #getKeys() key} that contains exactly those columns listed.
         * 
         * @param columns the columns for the key
         * @return true if this table contains a key using exactly the supplied columns, or false otherwise
         */
        boolean hasKey( Iterable<Column> columns );

        /**
         * Obtain this table's {@link #getKeys() key} that contains exactly those columns listed.
         * 
         * @param columns the columns for the key
         * @return the key that uses exactly the supplied columns, or null if there is no such key
         */
        Key getKey( Column... columns );

        /**
         * Obtain this table's {@link #getKeys() key} that contains exactly those columns listed.
         * 
         * @param columns the columns for the key
         * @return the key that uses exactly the supplied columns, or null if there is no such key
         */
        Key getKey( Iterable<Column> columns );
    }

    /**
     * Information about a queryable column.
     */
    public interface Column {
        /**
         * Get the name for this column.
         * 
         * @return the column name; never null
         */
        String getName();

        /**
         * Get the property type for this column.
         * 
         * @return the property type; never null
         */
        PropertyType getPropertyType();
    }

    /**
     * Information about a key for a table.
     */
    public interface Key {
        /**
         * Get the columns that make up this key.
         * 
         * @return the key's columns; immutable and never null
         */
        Set<Column> getColumns();

        /**
         * Determine whether this key contains exactly those columns listed.
         * 
         * @param columns the columns for the key
         * @return true if this key contains exactly the supplied columns, or false otherwise
         */
        boolean hasColumns( Column... columns );

        /**
         * Determine whether this key contains exactly those columns listed.
         * 
         * @param columns the columns for the key
         * @return true if this key contains exactly the supplied columns, or false otherwise
         */
        boolean hasColumns( Iterable<Column> columns );
    }

}
