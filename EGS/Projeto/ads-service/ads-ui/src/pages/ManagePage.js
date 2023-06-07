import { Helmet } from 'react-helmet-async';
import { filter } from 'lodash';
import { sentenceCase } from 'change-case';
import { useState } from 'react';
// @mui
import {
  Card,
  Table,
  Stack,
  Paper,
  Avatar,
  Button,
  Popover,
  Checkbox,
  TableRow,
  MenuItem,
  TableBody,
  TableCell,
  Container,
  Typography,
  IconButton,
  TableContainer,
  TablePagination,
} from '@mui/material';
// components
import Label from '../components/label';
import Iconify from '../components/iconify';
import Scrollbar from '../components/scrollbar';
// sections
import { UserListHead, UserListToolbar } from '../sections/@dashboard/user';
// mock
import USERLIST from '../_mock/user';

import { useNavigate } from 'react-router-dom';
import { useEffect } from 'react';

// ----------------------------------------------------------------------

const TABLE_HEAD = [
    { id: 'id', label: 'ID', alignRight: false },
    { id: 'desc', label: 'Description', alignRight: false },
    { id: 'model', label: 'Pricing model', alignRight: false },
    { id: 'impressions', label: 'Impressions', alignRight: false },
    { id: 'clicks', label: 'Clicks', alignRight: false },
    { id: 'target', label: 'Goal', alignRight: false },
    { id: '' },
];

// ----------------------------------------------------------------------

function descendingComparator(a, b, orderBy) {
  if (b[orderBy] < a[orderBy]) {
    return -1;
  }
  if (b[orderBy] > a[orderBy]) {
    return 1;
  }
  return 0;
}

function getComparator(order, orderBy) {
  return order === 'desc'
    ? (a, b) => descendingComparator(a, b, orderBy)
    : (a, b) => -descendingComparator(a, b, orderBy);
}

function applySortFilter(array, comparator, query) {
  const stabilizedThis = array.map((el, index) => [el, index]);
  stabilizedThis.sort((a, b) => {
    const order = comparator(a[0], b[0]);
    if (order !== 0) return order;
    return a[1] - b[1];
  });
  if (query) {
    return filter(array, (_user) => _user.description.toLowerCase().indexOf(query.toLowerCase()) !== -1);
  }
  return stabilizedThis.map((el) => el[0]);
}

export default function UserPage() {

    const [ads, setAds] = useState([]);
    const hostAddress = window.location.host;
    const [ip, port] = hostAddress.split(':');

    const navigate = useNavigate();

    //on page load, read user from props

    useEffect(() => {
        if(localStorage.getItem('type') == '"C"'){
            console.log("user is a consumer");
            navigate('/consumer');
            return;
        }
        if (localStorage.getItem('email') != null) {
            console.log(localStorage.getItem('email') + " is logged in");
            fetch('http://ads-api-mixit.deti/v1/profile', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + localStorage.getItem('token').replace(/['"]+/g, ''),
                },
            })
            .then(response => response.json())
            .then(data => {
                console.log(data.ads);
                setAds(data.ads);
            });
        }
        else {
            console.log("no user");
            navigate('/login');
        }
    }, []);


    const [open, setOpen] = useState(null);

    const [ id, setId ] = useState(null);

    const [page, setPage] = useState(0);

    const [order, setOrder] = useState('asc');

    const [selected, setSelected] = useState([]);

    const [orderBy, setOrderBy] = useState('name');

    const [filterName, setFilterName] = useState('');

    const [rowsPerPage, setRowsPerPage] = useState(5);

    const handleOpenMenu = (event, id) => {
        setOpen(event.currentTarget);
        setId(id);
    };

    const handleCloseMenu = () => {
        setOpen(null);
    };

    const handleRequestSort = (event, property) => {
        const isAsc = orderBy === property && order === 'asc';
        setOrder(isAsc ? 'desc' : 'asc');
        setOrderBy(property);
    };

    const handleSelectAllClick = (event) => {
        if (event.target.checked) {
        const newSelecteds = ads.map((n) => n.name);
        setSelected(newSelecteds);
        return;
        }
        setSelected([]);
    };

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setPage(0);
        setRowsPerPage(parseInt(event.target.value, 10));
    };

    const handleFilterByName = (event) => {
        setPage(0);
        setFilterName(event.target.value);
    };

    const handleNewAd = () => {
        navigate('/new-ad');
        };

    const handleDeleteAd = () => {
        fetch('http://'+ip+':8010/v1/ads/' + id, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem('token').replace(/['"]+/g, ''),
            },
        })
        .then((response) => {
            if (response.status === 200) {
                console.log("deleted");
                fetch('http://'+ip+':8010/v1/profile', {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + localStorage.getItem('token').replace(/['"]+/g, ''),
                    },
                })
                .then(response => response.json())
                .then(data => {
                    console.log(data.ads);
                    setAds(data.ads);
                });
            } else {
                console.log("error");
            }
        }
        )

        handleCloseMenu();
        
    };

    const emptyRows = page > 0 ? Math.max(0, (1 + page) * rowsPerPage - ads.length) : 0;

    const filteredAds = applySortFilter(ads, getComparator(order, orderBy), filterName);

    const isNotFound = !filteredAds.length && !!filterName;

    return (
        <>
        <Helmet>
            <title> Management </title>
        </Helmet>

        <Container>
            <Stack direction="row" alignItems="center" justifyContent="space-between" mb={6}>
            <Typography variant="h4" gutterBottom>
                Management
            </Typography>
            <Button variant="contained" startIcon={<Iconify icon="eva:plus-fill" />} onClick={handleNewAd}>
                New Advertisement
            </Button>
            </Stack>

            <Card>
            <UserListToolbar numSelected={selected.length} filterName={filterName} onFilterName={handleFilterByName} />

            <Scrollbar>
                <TableContainer sx={{ minWidth: 800 }}>
                <Table>
                    <UserListHead
                    order={order}
                    orderBy={orderBy}
                    headLabel={TABLE_HEAD}
                    rowCount={ads.length}
                    numSelected={selected.length}
                    onRequestSort={handleRequestSort}
                    onSelectAllClick={handleSelectAllClick}
                    />
                    <TableBody>
                    {filteredAds.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map((row) => {
                        const { id, description, pricing_model, impressions, clicks, target } = row;

                        return (
                        <TableRow hover key={id} tabIndex={-1}>
                            <TableCell padding="checkbox">
                            </TableCell>

                            <TableCell align="left">{id}</TableCell>

                            <TableCell align="left">{description}</TableCell>

                            <TableCell align="left">{pricing_model}</TableCell>

                            <TableCell align="left">{impressions}</TableCell>

                            <TableCell align="left">{clicks}</TableCell>

                            <TableCell align="left">{target}</TableCell>

                            <TableCell align="right">
                                <IconButton size="large" color="inherit" onClick={(event) => handleOpenMenu(event, id)}>
                                    <Iconify icon={'eva:more-vertical-fill'} />
                                </IconButton>
                            </TableCell>
                        </TableRow>
                        );
                    })}
                    {emptyRows > 0 && (
                        <TableRow style={{ height: 53 * emptyRows }}>
                        <TableCell colSpan={6} />
                        </TableRow>
                    )}
                    </TableBody>

                    {isNotFound && (
                    <TableBody>
                        <TableRow>
                        <TableCell align="center" colSpan={6} sx={{ py: 3 }}>
                            <Paper
                            sx={{
                                textAlign: 'center',
                            }}
                            >
                            <Typography variant="h6" paragraph>
                                Not found
                            </Typography>

                            <Typography variant="body2">
                                No results found for &nbsp;
                                <strong>&quot;{filterName}&quot;</strong>.
                                <br /> Try checking for typos or using complete words.
                            </Typography>
                            </Paper>
                        </TableCell>
                        </TableRow>
                    </TableBody>
                    )}
                </Table>
                </TableContainer>
            </Scrollbar>

            <TablePagination
                rowsPerPageOptions={[5, 10, 25]}
                component="div"
                count={ads.length}
                rowsPerPage={rowsPerPage}
                page={page}
                onPageChange={handleChangePage}
                onRowsPerPageChange={handleChangeRowsPerPage}
            />
            </Card>
        </Container>

        <Popover
            open={Boolean(open)}
            anchorEl={open}
            onClose={handleCloseMenu}
            anchorOrigin={{ vertical: 'top', horizontal: 'left' }}
            transformOrigin={{ vertical: 'top', horizontal: 'right' }}
            PaperProps={{
            sx: {
                p: 1,
                width: 140,
                '& .MuiMenuItem-root': {
                px: 1,
                typography: 'body2',
                borderRadius: 0.75,
                },
            },
            }}
        >

            <MenuItem sx={{ color: 'error.main' }} onClick={handleDeleteAd}>
            <Iconify icon={'eva:trash-2-outline'} sx={{ mr: 2 }} />
            Delete
            </MenuItem>
        </Popover>
        </>
    );
}
