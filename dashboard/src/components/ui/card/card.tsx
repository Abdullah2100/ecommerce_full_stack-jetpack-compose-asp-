import * as React from "react"

import { cn } from "@/lib/utils"

interface CardProps {
  title?: string
  additionalIcon?: React.ReactNode
  content?: React.ReactNode
}

const Card = React.forwardRef<HTMLDivElement, CardProps>(
  ({ title, content, additionalIcon }, ref) => {
    return (
      <div
        ref={ref}
        className={cn("rounded-xl border bg-card text-card-foreground shadow")}>
        {(title || content) && (
          <div className="flex flex-col space-y-1.5 p-6">
            <div className="flex items-start justify-between gap-4">
              <div className="space-y-1">
                {title && (
                  <div className="flex flex-row items-center justify-between space-y-0">
                    <h3 className="font-semibold leading-none tracking-tight mr-2">
                      {title}
                    </h3>
                    {additionalIcon && additionalIcon}
                  </div>
                )}
                {content && content}
              </div>
            </div>
          </div>
        )}
      </div>
    )
  }
)
Card.displayName = "Card"

export { Card }
