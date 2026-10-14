"use client";
import { useState } from "react";
import { Search, UserCheck, UserX } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Skeleton } from "@/components/ui/skeleton";
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog";
import {
  useAdminUsers,
  useEnableUser,
  useDisableUser,
} from "@/services/user.service";
import { toast } from "sonner";
import { cn } from "@/lib/utils";

export default function UsersPage() {
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState("");
  const { data: usersPage, isLoading } = useAdminUsers(page, 15);
  const enableUser = useEnableUser();
  const disableUser = useDisableUser();

  const users = (usersPage?.content ?? []).filter(
    (u) =>
      !search ||
      `${u.firstName} ${u.lastName} ${u.email}`
        .toLowerCase()
        .includes(search.toLowerCase()),
  );
  const totalPages = usersPage?.totalPages ?? 1;

  const handleEnable = async (id: string) => {
    try {
      await enableUser.mutateAsync(id);
      toast.success("User enabled");
    } catch {
      toast.error("Failed to enable user");
    }
  };
  const handleDisable = async (id: string) => {
    try {
      await disableUser.mutateAsync(id);
      toast.success("User disabled");
    } catch {
      toast.error("Failed to disable user");
    }
  };

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Users</h1>
      <div className="relative w-72">
        <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
        <Input
          placeholder="Search users…"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="pl-8"
        />
      </div>
      <div className="border rounded-xl overflow-hidden bg-white">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b">
            <tr>
              <th className="text-left px-4 py-3 font-medium text-gray-500">
                User
              </th>
              <th className="text-left px-4 py-3 font-medium text-gray-500">
                Phone
              </th>
              <th className="text-left px-4 py-3 font-medium text-gray-500">
                Status
              </th>
              <th className="text-left px-4 py-3 font-medium text-gray-500">
                Roles
              </th>
              <th className="text-left px-4 py-3 font-medium text-gray-500">
                Joined
              </th>
              <th className="px-4 py-3" />
            </tr>
          </thead>
          <tbody className="divide-y">
            {isLoading
              ? [1, 2, 3, 4].map((i) => (
                  <tr key={i}>
                    <td colSpan={6} className="px-4 py-3">
                      <Skeleton className="h-5" />
                    </td>
                  </tr>
                ))
              : users.map((user) => (
                  <tr key={user.id} className="hover:bg-gray-50">
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-3">
                        <Avatar className="w-8 h-8">
                          <AvatarFallback className="text-xs bg-indigo-100 text-indigo-700">
                            {user.firstName[0]}
                            {user.lastName[0]}
                          </AvatarFallback>
                        </Avatar>
                        <div>
                          <p className="font-medium">
                            {user.firstName} {user.lastName}
                          </p>
                          <p className="text-xs text-gray-400">{user.email}</p>
                        </div>
                      </div>
                    </td>
                    <td className="px-4 py-3 text-gray-500">
                      {user.phoneNumber}
                    </td>
                    <td className="px-4 py-3">
                      <span
                        className={cn(
                          "text-xs px-2 py-0.5 rounded-full font-medium",
                          user.enabled
                            ? "bg-green-100 text-green-700"
                            : "bg-red-100 text-red-700",
                        )}
                      >
                        {user.enabled ? "Active" : "Disabled"}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-gray-500 text-xs">
                      {user.roles.map((r) => r.name).join(", ") || "—"}
                    </td>
                    <td className="px-4 py-3 text-gray-400 text-xs">
                      {new Date(user.createdDate).toLocaleDateString()}
                    </td>
                    <td className="px-4 py-3">
                      {user.enabled ? (
                        <AlertDialog>
                          <AlertDialogTrigger asChild>
                            <Button
                              variant="ghost"
                              size="sm"
                              className="text-red-400 hover:text-red-600 h-7 text-xs"
                            >
                              <UserX className="w-3.5 h-3.5 mr-1" />
                              Disable
                            </Button>
                          </AlertDialogTrigger>
                          <AlertDialogContent>
                            <AlertDialogHeader>
                              <AlertDialogTitle>Disable user?</AlertDialogTitle>
                              <AlertDialogDescription>
                                This will prevent them from logging in.
                              </AlertDialogDescription>
                            </AlertDialogHeader>
                            <AlertDialogFooter>
                              <AlertDialogCancel>Cancel</AlertDialogCancel>
                              <AlertDialogAction
                                onClick={() => handleDisable(user.id)}
                                className="bg-red-600 hover:bg-red-700"
                              >
                                Disable
                              </AlertDialogAction>
                            </AlertDialogFooter>
                          </AlertDialogContent>
                        </AlertDialog>
                      ) : (
                        <Button
                          variant="ghost"
                          size="sm"
                          className="text-green-600 hover:text-green-700 h-7 text-xs"
                          onClick={() => handleEnable(user.id)}
                        >
                          <UserCheck className="w-3.5 h-3.5 mr-1" />
                          Enable
                        </Button>
                      )}
                    </td>
                  </tr>
                ))}
          </tbody>
        </table>
        {!isLoading && users.length === 0 && (
          <p className="text-center text-gray-400 py-12">No users found</p>
        )}
      </div>
      {totalPages > 1 && (
        <Pagination>
          <PaginationContent>
            <PaginationItem>
              <PaginationPrevious
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                className={
                  page === 0
                    ? "pointer-events-none opacity-50"
                    : "cursor-pointer"
                }
              />
            </PaginationItem>
            <PaginationItem>
              <span className="px-4 py-2 text-sm">
                Page {page + 1} of {totalPages}
              </span>
            </PaginationItem>
            <PaginationItem>
              <PaginationNext
                onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                className={
                  page >= totalPages - 1
                    ? "pointer-events-none opacity-50"
                    : "cursor-pointer"
                }
              />
            </PaginationItem>
          </PaginationContent>
        </Pagination>
      )}
    </div>
  );
}
