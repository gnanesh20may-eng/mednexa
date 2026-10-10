package com.example.myapplication.core

/**
 * Status indicator for stock levels and consumption trends.
 */
enum class StockStatus {
    /**
     * Critical status (Red): Stock will run out before the next indent period.
     */
    AT_RISK,

    /**
     * Speed-up status (Amber/Orange): Daily use doubled week-over-week (>= 200%).
     */
    SPEED_UP,

    /**
     * Warning status (Amber): Usage rate has increased by 25% or more compared to previous 7 days.
     */
    ACCELERATING,

    /**
     * Normal status (Green): Adequate stock and stable consumption rate.
     */
    OK
}
